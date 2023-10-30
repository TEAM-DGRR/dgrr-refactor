import json

from app.websocket_server.utils import send_to_java_server
from app.core.logger_config import get_logger

logger = get_logger(__name__)


async def on_connect(websocket):
    logger.info("새로운 클라이언트가 연결되었습니다.")
    await websocket.send_text("Python 서버와 Websocket 연결에 성공하였습니다.")


async def on_disconnect(websocket):
    logger.info("클라이언트와의 연결이 끊어졌습니다.")


async def handle_message(message, app):
    json_data = json.loads(message)
    header, image_data = json_data["header"], json_data["image"]

    await send_to_java_server(header, image_data, app)
