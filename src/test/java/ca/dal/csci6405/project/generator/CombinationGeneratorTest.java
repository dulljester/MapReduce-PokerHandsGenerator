package ca.dal.csci6405.project.generator;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sj on 28/03/17.
 */
public class CombinationGeneratorTest extends TestCase {
    private final long LIMIT = 2500000L;
    public void testBruteForce() {
        long cnt = 0;
        CombinationGenerator generator = new CombinationGenerator();
        for ( int i = 0; i+4 < 52; ++i )
            for ( int j = i+1; j+3 < 52; ++j )
                for ( int k = j+1; k+2 < 52; ++k )
                    for ( int l = k+1; l+1 < 52; ++l )
                        for ( int t = l+1; t < 52; ++t ) {
                            int []c = {i,j,k,l,t}, d = new int[5];
                            List<Integer> lst = generator.generateLexCombination(cnt++);
                            assertNotNull( lst );
                            for ( int o = 0; o < 5; d[o] = lst.get(o), ++o ) ;
                            assertEquals(Arrays.equals(c,d),true);
                            if ( cnt == LIMIT ) return ;
                        }
    }
}
