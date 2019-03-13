// Copyright (c) 2017 Uber Technologies, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package app

import (
	"go.uber.org/zap"
	"io"

	"github.com/jaegertracing/jaeger/cmd/agent/app/processors"
)

// Agent is a composition of all services / components
type Agent struct {
	processors []processors.Processor
	logger     *zap.Logger
	closer     io.Closer
}

// NewAgent creates the new Agent.
func NewAgent(
	processors []processors.Processor,
	logger *zap.Logger,
) *Agent {
	a := &Agent{
		processors: processors,
		logger:     logger,
	}
	return a
}

// Run runs all of agent UDP and HTTP servers in separate go-routines.
// It returns an error when it's immediately apparent on startup, but
// any errors happening after starting the servers are only logged.
func (a *Agent) Run() error {
	for _, processor := range a.processors {
		go processor.Serve()
	}
	return nil
}

// Stop forces all agent go routines to exit.
func (a *Agent) Stop() {
	for _, processor := range a.processors {
		go processor.Stop()
	}
	a.closer.Close()
}
