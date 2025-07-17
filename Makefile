all: build run

rebuild: clean build

build:
    ./gradlew build
    ./gradlew shadowJar

run:
    java -jar build/libs/Abstract-1.0-Alpha-all.jar

clean:
    rm -fr build/
