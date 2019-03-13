#To be run in folder /go/src/github.com/jaegertracing 
#Download repo from Github, version 1.5
#Substitute content of folder kaiju-agent-code in folder
#/go/src/github.com/jaegertracing/jaeger/cmd/agent/

# build stage
FROM golang:alpine AS build-env
WORKDIR /app
# Set an env var that matches your path
ENV SRC_DIR=/go/src/github.com/jaegertracing/jaeger/
# Add the source code:
ADD jaeger/ $SRC_DIR
# Build it:
RUN cd $SRC_DIR; cd cmd/agent/; go build main.go; cp main /app/

# final stage
FROM alpine
WORKDIR /app
COPY --from=build-env /app/main /app/
#ENTRYPOINT ["./main"]