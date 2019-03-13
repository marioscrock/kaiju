FROM golang:alpine AS build-env
WORKDIR /app
ADD app/ .
# Build it:
RUN go build makeRequests.go makeRequestsTimes.go

# final stage
FROM alpine
WORKDIR /app
COPY --from=build-env /app/makeRequests /app/
ENTRYPOINT ["./makeRequests"]
