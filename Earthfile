FROM hseeberger/scala-sbt:11.0.8_1.4.0_2.13.3
WORKDIR /build

deps:
    COPY build.sbt ./
    COPY project project
    RUN sbt update
    SAVE IMAGE

build:
    FROM +deps
    COPY src src
    RUN sbt compile 

deploy:
    FROM +deps
	COPY src src
	RUN sbt assembly