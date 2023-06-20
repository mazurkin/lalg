SHELL := /bin/bash

DOCKER_NAME := com.github.mazurkin/lalg

export JAVA_HOME=${JAVA11_HOME}

.PHONY:\
	info \
	build rsync \
	docker-build docker-run docker-prune

info:
	@echo "JDK:${JAVA_HOME}"

build:
	@mvn --batch-mode clean package

docker-build: build
	$(eval DOCKER_VERSION := $(shell mvn -f pom.xml help:evaluate -Dexpression=project.version --quiet -DforceStdout))
	$(eval DOCKER_IMAGE   := $(DOCKER_NAME):$(DOCKER_VERSION))
	@docker build -t "$(DOCKER_IMAGE)" .

docker-run: info
	$(eval DOCKER_VERSION := $(shell mvn -f pom.xml help:evaluate -Dexpression=project.version --quiet -DforceStdout))
	$(eval DOCKER_IMAGE   := $(DOCKER_NAME):$(DOCKER_VERSION))
	@docker run \
		--rm \
		--name "lalg" \
		--hostname="lalg" \
		--interactive \
		--tty \
		--read-only \
		--volume "/tmp/lalg:/tmp/" \
		"$(DOCKER_IMAGE)"

docker-prune:
	@docker image prune --force
