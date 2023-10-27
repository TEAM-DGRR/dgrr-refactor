import json

from app.shared.image_utils import image_data_to_np, analyze_image
from app.core.config import settings
from logger_config import get_logger

logger = get_logger(__name__)


async def send_to_java_server(header, image_data, app):
    stomp = app.state.stomp
    if stomp is None or not stomp.open:
        logger.error("자바 서버와의 연결이 끊겼습니다. 데이터를 보내지 않습니다.")
        return

    try:
        encoded_data = image_data.split(",")[1]
        image = await image_data_to_np(encoded_data)
        sended_data = await analyze_image(image=image, app=app)
        sended_data["header"] = header

        if (
            sended_data["emotion"] == "Smile"
            and sended_data["probability"] > settings.THRESHOLD
        ):
            sended_data["encodedImage"] = encoded_data
        else:
            sended_data["encodedImage"] = None

        sended_data_json = json.dumps(sended_data)
        send_message = (
            f"SEND\ndestination:{settings.CAPTURE_IMAGE_DEST}\n\n{sended_data_json}\0"
        )
        await stomp.send(send_message)
        logger.info(f"분석 결과를 전송했습니다")
    except Exception as e:
        logger.warning("Publish 에러 발생!! ", e)
