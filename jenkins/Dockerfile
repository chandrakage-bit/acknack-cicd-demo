# Jenkins with Maven, Docker CLI, kubectl and kind baked in (practice use only)
FROM jenkins/jenkins:lts-jdk17
USER root
RUN apt-get update \
 && apt-get install -y --no-install-recommends maven curl ca-certificates \
 && rm -rf /var/lib/apt/lists/*
RUN ARCH=$(dpkg --print-architecture) \
 && DARCH=$(uname -m) \
 && curl -fsSL "https://download.docker.com/linux/static/stable/${DARCH}/docker-27.3.1.tgz" \
    | tar -xz --strip-components=1 -C /usr/local/bin docker/docker \
 && curl -fsSLo /usr/local/bin/kubectl "https://dl.k8s.io/release/$(curl -fsSL https://dl.k8s.io/release/stable.txt)/bin/linux/${ARCH}/kubectl" \
 && curl -fsSLo /usr/local/bin/kind "https://kind.sigs.k8s.io/dl/v0.23.0/kind-linux-${ARCH}" \
 && chmod +x /usr/local/bin/docker /usr/local/bin/kubectl /usr/local/bin/kind
