from fastapi import APIRouter, WebSocket, WebSocketDisconnect

from app.websocket_server.handler import on_connect, on_disconnect, handle_message
from app.core.logger_config import get_logger

from app.core.config import settings

router = APIRouter()
logger = get_logger(__name__)


@router.websocket(settings.WEBSOCKET_CONNECT_URI)
async def receive_capture(websocket: WebSocket):
    await websocket.accept()
    try:
        await on_connect(websocket)
        while True:
            message = await websocket.receive_text()
            await handle_message(message, websocket.app)

    except WebSocketDisconnect:
        await on_disconnect(websocket)
        #await websocket.close()

    except Exception as e:
        logger.error(f"An error occurred: {str(e)}")
        await websocket.close()
