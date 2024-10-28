FROM jenkins/jenkins:jdk17

USER root
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y openssh-client \
    curl \
    gnupg2 \
    lsb-release \
    software-properties-common

# Docker 설치
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add - && \
    add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable" && \
    apt-get update && \
    apt-get install -y docker-ce-cli

# Docker Compose 설치
RUN curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose

# Docker 그룹을 수동으로 생성
RUN groupadd docker && usermod -aG docker jenkins

# SSH 설치 (필요한 경우)
RUN apt-get install -y openssh-client