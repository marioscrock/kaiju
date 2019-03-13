# Kaiju

<!---
The content of this repository is discussed in details in:
```
A Success Story of Event-Driven Observability in Production-Grade Container Orchestration
Authors: Mario Scrocca, Riccardo Tommasini, Alessandro Margara and Emanuele Della Valle
```
-->
  
_Kaiju_ is an artifact based on the [Esper engine](http://www.espertech.com/) and implementing event-driven observability in container orchestration through Kubernetes.

Key aspects:
- _Kaiju_ can process observations (i.e. metrics, logs and traces) as events in real-time
- _Kaiju_ is an easily-pluggable solution for companies already implementing projects of the CNCF stack
- _Kaiju_ implements a modular solution to take into account the different processing required in dealing with metrics, logs and traces
- _Kaiju_ enables the definition and processing of custom and configurable types of events between different components

<p align="center"><img src="kaiju-architecture.png" alt="Kaiju Architecture" width="500"></p>

## Respository structure

The repository is organized in 5 main folders:
- `kaiju`: Contains the source file for _Kaiju_ modules.
- `rim`: Contains a modified version of the [HotR.O.D.](https://github.com/jaegertracing/jaeger/tree/v1.5.0/examples/hotrod) demo application by Uber.
- `kube`: Contains manifests to launch an environment for experiments using a Kubernetes cluster.
- `fluentd-kaiju`: Contains files to build the modified _fluentd_ image reporting logs to _Kaiju_.
- `kaiju-agent`: Contains files to build the modified agent reporting traces to _Kaiju_.

## `kaiju`

Kaiju is composed of four different types of modules, each one can collect a different sort of data, and can forward generic events modelled as POJO (`Event.java` class in the Kaiju project). The modularization enables horizontal scalability for each type of module and the hierarchical composition of \emph{kaiju-hl} modules. 

The `Kaiju` image can be built using the `DockerFile` provided and then launched with different arguments to execute the four different models. Arguments are:
- `--mode` or `-m` to set the type of module to execute. Values can be `traces`,`logs`,`metrics`, `high-level`

#### `kaiju-metrics`

 receives data from an agent that pulls Prometheus endpoints. We choose to adopt the \emph{Telegraf} agent\footnote{Telegraf https://www.influxdata.com/time-series-platform/telegraf/} from Influx and its plugin to scrape Prometheus endpoints. This plugin allows to manage simple endpoints, multiple endpoints load-balanced from a service and  resources annotated for scraping. Deploying a set of agents in the different nodes of the cluster allows to collect metrics forwarding them to the \emph{kaiju-metrics} component. We model each metric as a POJO taking into account the Telegraf output format: the timestamp when the metric were collected, a name, a key-value map of tags as labels and a key-value map of fields representing values sampled.

\vspace{2pt}

#### `kaiju-logs` 

receives logs from 
\emph{Fluentd}. We implemented an output plugin able to forward data to the \emph{Kaiju-Logs} component. \emph{Fluentd} enables both pull and push paradigm providing input plugins for both alternatives. We model each log as a POJO with a key-value map of fields. The main problem with logs is related to the multitude of different formats, often not structured and difficult to parse. For this reason, we choose to adopt the ingestion time as timestamp for logs and to process them trying to flatten JSON based syntax in a map of fields

#### `kaiju-traces`
\noindent\textbf{Kaiju-Traces} receives spans from an agent deployed aside the process. We choose to exploit language-dependent client libraries provided by \emph{Jaeger} and we implement a custom version of the \emph{jaeger-agent} reporting them to the \emph{kaiju-traces} component. As in \emph{Jaeger} the agent receives spans in push on a UDP port and forwards data to the specified collector. We model each span and its contained events as a set of POJOs, as defined in the internal \emph{Jaeger} model. We consider as timestamp for spans the ingestion time.

\vspace{2pt}

\noindent\textbf{Kaiju-HL} accepts socket connections from other modules to collect events. \emph{kaiju-metrics}, \emph{kaiju-logs}, \emph{kaiju-traces} or other \emph{kaiju-hl} modules can forward data assigning statements to a specific listener. \emph{kaiju-hl} accepts only events parsable to the POJO described in Listing~\ref{lst:event}, but offers the possibility to configure from file a set of different events that are extracted from this incoming stream. The configuration files requires to specify for each event, the name of the event, pairs key-datatype for payload, pairs key-datatype for context, name of inherited events. For each event configured it is automatically created:
\begin{inparaenum} [(i)]
\item a \texttt{create schema} statement;
\item an \texttt{insert into} statement from the \texttt{Event} stream, checking existence of keys in the payload to identify items and selecting event properties from cast payload/context.
\end{inparaenum}

Experiments in the paper can be run downloading `kustomize` and setting up `kubectl` to communicate with a Kubernetes cluster. `kube` folder contains manifests.

```
cd kube
kustomize build | kubectl apply -f -
```

To guarantee anonymization we do not add links to repositories with built images. Each folder contains the related Docker file to build the images: `kaiju` , `rim` ,`fluentd-kaiju` , `kaiju-agent` .

## Additional notes

- To launch the load test you should build the image `hotrod-load` in folder `kube/load` and deploy the Kubernetes Job.
- Syntax of `statements.txt` files is: `key-config` : `value-config` `[,key-config:value-config]*`=`statement`. One statement for each line. `#` at the beginning of a line comments it out.

* One statement for line, the # at the beginning comments out the line.- Syntax of `events.txt` files is: `event-name`>{`payload-key`:`type` `[,payload-key:type]*`}>{`context-key`:`type` `[,context-key:type]*`}>{`inherits-event-name` `[,inherits-event-name]*`}
