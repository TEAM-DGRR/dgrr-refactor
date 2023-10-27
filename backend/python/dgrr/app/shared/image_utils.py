import asyncio, cv2, base64, concurrent.futures
import numpy as np

from logger_config import get_logger

logger = get_logger(__name__)


async def image_data_to_np(image: str):
    try:
        image_data = base64.b64decode(image)

        image_array = np.frombuffer(image_data, dtype=np.uint8)

        result = cv2.imdecode(image_array, flags=cv2.IMREAD_COLOR)

        if result is None:
            logger.error("cv2.imdecode 실패")
        else:
            return result
    except Exception as e:
        logger.error(f"이미지 데이터 변환 오류 발생: {e}")
        return None


async def analyze_image(image, app):
    try:
        loop = asyncio.get_running_loop()

        with concurrent.futures.ThreadPoolExecutor() as pool:
            result = await loop.run_in_executor(
                pool,
                lambda: _analyze_image_sync(image, app),
            )
        return result

    except Exception as e:
        logger.error(f"analyze_image에서 예외 발생: {str(e)}")
        return {
            "success": False,
            "emotion": "Error",
            "probability": 0.0,
            "smileProbability": 0.0,
        }


def _analyze_image_sync(image, app):
    try:
        face_cascade = app.state.face_cascade
        emotion_model = app.state.emotion_model
        emotions = app.state.emotions

        gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        faces = face_cascade.detectMultiScale(gray_image, 1.3, 5)

        for x, y, w, h in faces:
            roi_gray = gray_image[y : y + h, x : x + w]
            roi_gray = cv2.resize(roi_gray, (64, 64), interpolation=cv2.INTER_AREA)

            roi_gray = roi_gray / 255.0

            roi_gray = np.expand_dims(roi_gray, axis=0)

            prediction = emotion_model.predict(roi_gray)
            probabilities = prediction[0]

            smile_index = emotions.index("Smile")
            smile_prob = probabilities[smile_index]

            max_index = np.argmax(probabilities)
            emotion = emotions[max_index]
            emotion_prob = probabilities[max_index]

            return {
                "success": True,
                "emotion": emotion,
                "probability": float(emotion_prob),
                "smileProbability": float(smile_prob),
            }
        return {
            "success": False,
            "emotion": "Not Detected",
            "probability": 0.0,
            "smileProbability": 0.0,
        }
    except Exception as e:
        logger.error(f"_analyze_image_sync에서 예외 발생 : {str(e)}")
        return {
            "success": False,
            "emotion": "Error",
            "probability": 0.0,
            "smileProbability": 0.0,
        }
