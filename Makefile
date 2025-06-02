.PHONY: all build run

all: build run

build:
	./gradlew build
	./gradlew shadowJar
run:
	java -jar build/libs/Abstract-1.0-SNAPSHOT-all.jar