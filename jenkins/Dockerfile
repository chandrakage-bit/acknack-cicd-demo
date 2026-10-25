# Jenkins with Maven, Docker CLI, kubectl and kind baked in (practice use only)
FROM jenkins/jenkins:lts-jdk17
USER root
ARG TARGETARCH
RUN apt-get update \
 && apt-get install -y --no-install-recommends docker.io maven curl ca-certificates \
 && rm -rf /var/lib/apt/lists/*
RUN curl -fsSLo /usr/local/bin/kubectl "https://dl.k8s.io/release/$(curl -fsSL https://dl.k8s.io/release/stable.txt)/bin/linux/${TARGETARCH}/kubectl" \
 && curl -fsSLo /usr/local/bin/kind "https://kind.sigs.k8s.io/dl/v0.23.0/kind-linux-${TARGETARCH}" \
 && chmod +x /usr/local/bin/kubectl /usr/local/bin/kind
