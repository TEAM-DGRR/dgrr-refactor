import asyncio, cv2
from fastapi import FastAPI
from keras.models import load_model

from app.stomp_client import keep_alive
from app.websocket_server import router
from app.core.config import settings
from logger_config import get_logger

app = FastAPI(title=settings.PROJECT_NAME)

app.include_router(router)

logger = get_logger(__name__)


@app.on_event("startup")
async def on_startup():
    logger.info("FastAPI 서비스를 시작합니다.")
    app.state.face_cascade = cv2.CascadeClassifier(str(settings.HAARCASCADE_PATH))
    app.state.emotion_model = load_model(str(settings.EMOTION_MODEL_PATH))
    app.state.emotions = settings.EMOTIONS
    app.state.stomp = None
    app.state.reconnect_delay = settings.RECONNECT_DELAY
    app.state.keep_alive_interval = settings.KEEP_ALIVE_INTERVAL
    asyncio.create_task(keep_alive(app, settings.BROKER_URL))
    pass


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)
