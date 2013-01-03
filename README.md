Deja vu
=======

A framework for recreating production bugs in a sandboxed environment.

There are three fundamental abstractions in Deja vu:


1. UseCase: This is the highlevel construct the framework knows how to execute. A use case implements a method run which gets executed by the framework. Use cases can not call other use cases. Whatever is passed as input to a use case must be immutable bacause the framework must be able to re-create the begin state. The immutability is not enforced by the framework so be careful not to mutate the input!

2. Step: if needed a use case can be split into steps. A step can be called within a use case or from another step, again via the framework. There are no immutability requirements to steps, since they are always called from code originating from a use case.

3. Provider: whenever there is an external dependency (as a database) or a dependency on something not re-producable (e.g. a timestamp or a random string), then you need to put the reading of this in a provider. Everything that is outputted from a provider must also be immutable. This is again to allow the framework to re-create what a given provider returned in case the use case must be re-created. A provider instance is injected into either a use case or step by the framework by using the @Autowire annotation (instantiation cannot be done manually because the tracability will then be impossible).


Read more at http://www.jayway.com/2013/01/03/debugging-your-production-bugs-with-deja-vu/