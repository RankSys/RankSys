package es.uam.eps.ir.ranksys.fast.preference;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public interface FasterPreferenceData<U, I> extends FastPreferenceData<U, I> {

    public int[] getUidxIidxs(final int uidx);

    public double[] getUidxVs(final int uidx);

    public int[] getIidxUidxs(final int iidx);

    public double[] getIidxVs(final int iidx);

}
