# Must be run with build context $GOPATH/src folder
# It assumes $GOPATH/src contains dependencies (go get not working)

FROM golang:alpine AS build-env
WORKDIR /app
ENV SRC_DIR=/go/src/github.com/Rim/

# Add the source code:
ADD github.com/Rim/ $SRC_DIR

# Add DEPENDENCIES 
ADD /github.com/apache/ /go/src/github.com/apache/
ADD /github.com/beorn7/ /go/src/github.com/beorn7/
ADD /github.com/codahale/ /go/src/github.com/codahale/
ADD /github.com/go-kit/ /go/src/github.com/go-kit/
ADD /github.com/rakyll/statik/fs /go/src/github.com/rakyll/statik/fs
ADD /github.com/golang/ /go/src/github.com/golang/
ADD /github.com/matttproud/ /go/src/github.com/matttproud/
ADD /github.com/opentracing/ /go/src/github.com/opentracing/
ADD /github.com/opentracing-contrib/ /go/src/github.com/opentracing-contrib/
ADD /github.com/prometheus/ /go/src/github.com/prometheus/
ADD /github.com/spf13/ /go/src/github.com/spf13/
ADD /github.com/uber/ /go/src/github.com/uber/
ADD /github.com/uber-go/ /go/src/github.com/uber-go/
ADD /github.com/VividCortex/ /go/src/github.com/VividCortex/

ADD /go.uber.org/atomic/ /go/src/go.uber.org/atomic/
ADD /go.uber.org/multierr/ /go/src/go.uber.org/multierr/
ADD /go.uber.org/zap/ /go/src/go.uber.org/zap/

ADD /golang.org/x/ /go/src/golang.org/x/

# Build it:
RUN cd $SRC_DIR; go build main.go; cp main /app/

# final stage
FROM alpine
WORKDIR /app
COPY --from=build-env /app/main /app/
#ENTRYPOINT ["./main", "all"]
