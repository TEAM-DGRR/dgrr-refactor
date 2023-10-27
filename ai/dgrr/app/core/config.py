from pydantic_settings import BaseSettings
from pathlib import Path


# FastAPI 설정
class Settings(BaseSettings):
    PROJECT_NAME: str = "DGRR Emotion"
    HAARCASCADE_PATH: Path = Path(
        "app/resources/models/haarcascade_frontalface_default.xml"
    )
    EMOTION_MODEL_PATH: Path = Path("app/resources/models/emotion_model.hdf5")

    EMOTIONS: list = ["Angry", "Disgust", "Fear", "Smile", "Sad", "Surprise", "Neutral"]
    THRESHOLD: float = 0.5

    RECONNECT_DELAY: int = 3
    KEEP_ALIVE_INTERVAL: int = 10

    # .env 주입
    WEBSOCKET_CONNECT_URI: str
    BROKER_URL: str
    CAPTURE_IMAGE_DEST: str

    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"


settings = Settings()
