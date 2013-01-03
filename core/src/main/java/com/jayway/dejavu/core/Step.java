package com.jayway.dejavu.core;

public abstract class Step<I,O> {

    private UseCase<?,?> useCase;
    void setUseCase( UseCase<?,?> useCase ) {
        this.useCase = useCase;
    }

    public abstract O run(I input);

    public <I,O> O run( Class<? extends Step<I,O>> clazz, I input ) {
        return useCase.run( clazz, input );
    }
}
