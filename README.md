Deja vu
=======

## Introduction
Deja vu is a tracing framework that allows you to create sandboxed runnable traces replicating traces from production environments.
It works by annotating the code you want traced and uses aspectj for weaving in tracing behaviour.

Traces consists of runnable code (a unit test) and such tests can also manually be constructed which means Deja vu can be seen as a test framework
as well.

Watch a presentation (JavaZone 2013): http://jz13.java.no/presentation.html?id=20bc29da


## Run/Re-run
The idea is that once a traced method has been run it is possible, by using the framework, to re-run it with the exact same execution path as the
original run.

The methods you want traced can done so by annotating them <code>@Traced</code>, example:

<pre>
@Traced
public void findUserById( String id ) {
   ///...
}
</pre>

The framework must be provided an implementation of the interface TraceCallback. This interface has a method that will be called
upon every completed call to <code>@Traced</code> methods.

## Pure/Impure
The goal of the framework is to produce a deterministic sandboxed runnable trace having the same execution path as the original.
For this to be possible the framework must know explicitly what methods have behaviour making it either environment dependent (e.g.
reading from a database) or non-deterministic (e.g. making an execution path decision based on a random number).

Pure code is defined as code without: randomization, user inputs, global time dependencies, dependencies to external systems
(integration points), or access to shared mutable state. (Note that pure does not refer to the functional definition, because
Deja vu pure code is allowed to have side effects).

For tracing to work all code that does not pass the "pure" definition must be annotated @Impure:

<pre>
@Impure
public String randomUUID() {
    return UUID.randomUUID().toString();
}
</pre>

For details see http://www.jayway.com/2013/01/03/debugging-your-production-bugs-with-deja-vu/

## Immutability
Inputs to <code>@Traced</code> methods and outputs from <code>@Impure</code> methods must be kept immutable
otherwise a re-run will not execute identically.

Note: The framework does not enforce this immutability constraint so be careful!

## Marshaling

Since Deja vu is about running code a Trace instance can be marshaled using the class Marshaller. A trace is marshaled to
the source code of a unit test.

For details see http://www.jayway.com/2013/01/05/json-marshaller-for-deja-vu-2/

## @AttachThread

It is possible to get traces even when multi threading is involved. However this requires the framework to understand when
parts of the trace is executed in another thread.

<pre>
@AttachThread
public void run( Runnable runnable ) {
    new Thread( runnable ).start();
}
</pre>

Using @AttachThread the framework will assume that all instances of runnable passed as arguments is about to be run
in a separate thread and the trace will only be completed when all such runnables are also finished running.

## Threaded re-run

When re-running a threaded trace the pure parts of the different threads will run parallel - so we are not guaranteed
the exact same ordering of instruction execution among threads (but this should not matter as this code is "pure").
What is guaranteed, however, is the order of execution of @Impure parts (the framework will simply let threads wait if
they tend to pass @Impure points before their time).

For details see http://www.jayway.com/2013/02/10/multi-threaded-traces-with-deja-vu/

## Setup

Since the framework is using aspectj there is an option of compile time weaving or runtime weaving. A typical setup
would be to have compile time weaving for production setup and compile time weaving for test setup.

### Compile time weaving

Using maven the following must be added to your pom.xml:
<pre>
    &lt;build&gt;
        &lt;plugins&gt;
            &lt;plugin&gt;
                &lt;groupId&gt;org.codehaus.mojo&lt;/groupId&gt;
                &lt;artifactId&gt;aspectj-maven-plugin&lt;/artifactId&gt;
                &lt;version&gt;1.4&lt;/version&gt;
                &lt;configuration&gt;
                    &lt;source&gt;1.6&lt;/source&gt;
                    &lt;target&gt;1.6&lt;/target&gt;
                    &lt;encoding&gt;utf-8&lt;/encoding&gt;
                    &lt;complianceLevel&gt;1.6&lt;/complianceLevel&gt;
                &lt;/configuration&gt;
                &lt;executions&gt;
                    &lt;execution&gt;
                        &lt;goals&gt;
                            &lt;goal&gt;compile&lt;/goal&gt;
                            &lt;goal&gt;test-compile&lt;/goal&gt;
                        &lt;/goals&gt;
                    &lt;/execution&gt;
                &lt;/executions&gt;
            &lt;/plugin&gt;
        &lt;/plugins&gt;
    &lt;/build&gt;
</pre>

Any classes using the Deja vu annotation must be specified in the <code>aop.xml</code> file (standard aspectj setup).

### Runtime weaving

Runtime weaving can be done by adding a javaagent argument to the VM options for the compiler:
<code>-javaagent:PATH TO ASPECTJWEAVER.jar</code>

