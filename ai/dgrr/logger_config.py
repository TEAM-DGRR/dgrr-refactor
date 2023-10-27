import logging
from termcolor import colored


class ColoredFormatter(logging.Formatter):
    COLORS = {
        "DEBUG": "white",
        "INFO": "green",
        "WARNING": "yellow",
        "ERROR": "red",
        "CRITICAL": "red",
    }

    def format(self, record):
        log_message = super().format(record)
        return colored(log_message, self.COLORS.get(record.levelname))


def get_logger(name):
    logger = logging.getLogger(name)
    logger.setLevel(logging.INFO)

    formatter = ColoredFormatter("%(asctime)s - %(name)s - %(levelname)s - %(message)s")

    stream_handler = logging.StreamHandler()
    stream_handler.setFormatter(formatter)

    logger.addHandler(stream_handler)

    return logger
