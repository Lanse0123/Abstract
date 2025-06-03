all: build run

rebuild: clean build

build:
    ./gradlew build
    ./gradlew shadowJar

run:
    java -jar build/libs/Abstract-1.0-SNAPSHOT-all.jar

clean:
    rm -fr build/
