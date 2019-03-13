package tchannel

import (

	"os"
	"time"

	"github.com/uber/tchannel-go"
	"github.com/uber/tchannel-go/thrift"
	"github.com/jaegertracing/jaeger/thrift-gen/jaeger"
	"github.com/jaegertracing/jaeger/thrift-gen/zipkincore"

	"go.uber.org/zap"
)

type KaijuReporter struct {
	channel        *tchannel.Channel
	jClient        jaeger.TChanCollector
	logger         *zap.Logger
}

func NewKaiju(zlogger *zap.Logger) *KaijuReporter {
	var channel *tchannel.Channel
	channel, _ = tchannel.NewChannel("kaiju-collector", nil)
	channel.Peers().Add(os.Getenv("KAIJU_ADDRESS"))
	thriftClient := thrift.NewClient(channel, "kaiju-collector", nil)
	jClient := jaeger.NewTChanCollectorClient(thriftClient)
	return &KaijuReporter{
		channel:        channel,
		jClient:        jClient,
		logger:         zlogger}
}

// EmitZipkinBatch implements EmitZipkinBatch() of Reporter
func (r *KaijuReporter) EmitZipkinBatch(spans []*zipkincore.Span) error {
	//Discard zipkin batches
	return nil
}

// EmitBatch implements EmitBatch() of Reporter
func (r *KaijuReporter) EmitBatch(batch *jaeger.Batch) error {
	submissionFunc := func(ctx thrift.Context) error {
		_, err := r.jClient.SubmitBatches(ctx, []*jaeger.Batch{batch})
		return err
	}
	return r.submitAndReport(
		submissionFunc,
		"Could not submit jaeger batch",
		int64(len(batch.Spans)))
}

func (r *KaijuReporter) submitAndReport(submissionFunc func(ctx thrift.Context) error, errMsg string, size int64) error {
	ctx, cancel := tchannel.NewContextBuilder(time.Second * time.Duration(10)).DisableTracing().Build()
	defer cancel()

	if err := submissionFunc(ctx); err != nil {
		r.logger.Error(errMsg, zap.Error(err))
		return err
	}

	r.logger.Debug("Span batch submitted by the agent", zap.Int64("span-count", size))
	return nil
}
