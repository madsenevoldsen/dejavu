package com.jayway.dejavu;

import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.impl.TestITRepo;
import com.jayway.dejavu.impl.UseCaseSetup;
import com.jayway.dejavu.impl.WorkingUseCase;
import com.jayway.dejavu.core.value.StringValue;
import junit.framework.Assert;
import org.junit.Test;

public class UseCaseTestTest {


    @Test
    public void createIntegrationTest() {
        TestITRepo repo = new TestITRepo();
        UseCaseSetup setup = new UseCaseSetup(repo);
        StringValue result = setup.run(WorkingUseCase.class, null);
        Assert.assertNotNull( result );
        Assert.assertTrue(result.getString().startsWith("My computer is so fast:"));

        Trace trace = repo.getTrace();
        Assert.assertNotNull( trace );
        Assert.assertEquals( WorkingUseCase.class, trace.getUseCaseClass() );


        TestGenerator generator = new TestGenerator();
        String it = generator.generateTest("com.jayway.app.UseCaseTestForWorkingUseCase", trace);
        System.out.println( it );
    }

}
