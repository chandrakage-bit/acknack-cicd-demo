# Jenkins with Maven, Docker CLI, kubectl and kind baked in (practice use only)
FROM docker:cli AS dockercli

FROM jenkins/jenkins:lts-jdk17
USER root
COPY --from=dockercli /usr/local/bin/docker /usr/local/bin/docker
RUN apt-get update \
 && apt-get install -y --no-install-recommends maven curl ca-certificates \
 && rm -rf /var/lib/apt/lists/*
RUN ARCH=$(dpkg --print-architecture) \
 && curl -fsSLo /usr/local/bin/kubectl "https://dl.k8s.io/release/$(curl -fsSL https://dl.k8s.io/release/stable.txt)/bin/linux/${ARCH}/kubectl" \
 && curl -fsSLo /usr/local/bin/kind "https://kind.sigs.k8s.io/dl/v0.23.0/kind-linux-${ARCH}" \
 && chmod +x /usr/local/bin/kubectl /usr/local/bin/kind
