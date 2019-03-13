# Kaiju

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