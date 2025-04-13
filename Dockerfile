FROM python:3.10-slim AS builder

ENV DEBIAN_FRONTEND=noninteractive
ENV PYTHONUNBUFFERED=1
ENV PIP_NO_CACHE_DIR=1

RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    curl \
    dnsutils \
    gcc \
    python3-dev \
    && rm -rf /var/lib/apt/lists/*

RUN python -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

RUN git clone --depth 1 https://github.com/laramies/theHarvester.git /opt/theHarvester \
    && pip install --no-cache-dir -r /opt/theHarvester/requirements.txt netaddr

RUN pip install --no-cache-dir git+https://github.com/sherlock-project/sherlock.git

WORKDIR /opt/spiderfoot
RUN git clone --depth 1 https://github.com/smicallef/spiderfoot.git . \
    && pip install --no-cache-dir -r requirements.txt

FROM python:3.10-slim

ENV PYTHONUNBUFFERED=1

RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    dnsutils \
    procps \
    supervisor \
    && rm -rf /var/lib/apt/lists/* /usr/share/doc /usr/share/man \
    && apt-get clean

COPY --from=builder /opt/venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

COPY --from=builder /opt/theHarvester /opt/theHarvester
COPY --from=builder /opt/spiderfoot /opt/spiderfoot

RUN ln -s /opt/theHarvester/theHarvester.py /usr/local/bin/theHarvester \
    && ln -s /opt/venv/bin/sherlock /usr/local/bin/sherlock


COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

COPY start-spiderfoot.sh /usr/local/bin/start-spiderfoot
COPY stop-spiderfoot.sh /usr/local/bin/stop-spiderfoot
RUN chmod +x /usr/local/bin/start-spiderfoot /usr/local/bin/stop-spiderfoot

WORKDIR /opt/spiderfoot

EXPOSE 5001

CMD ["supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
