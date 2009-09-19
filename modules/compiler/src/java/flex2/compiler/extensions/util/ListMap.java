package flex2.compiler.extensions.util;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Andrew Westberg
 */
public class ListMap<K, V>
    extends LinkedHashMap<K, Set<V>>
{
    private static final long serialVersionUID = 3990241918261742176L;

    public Set<V> put( K key, V value )
    {
        Set<V> list = get( key );
        list.add( value );
        return list;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Set<V> get( Object key )
    {
        Set<V> set = super.get( key );
        if ( set == null )
        {
            set = new LinkedHashSet<V>();
            put( (K) key, set );
        }
        return set;
    }

    @SuppressWarnings( "unchecked" )
    public <E> Set<E> get( K key, Class<E> clazz )
    {
        Set<V> list = get( key );

        Set<E> casted = new LinkedHashSet<E>();

        for ( V v : list )
        {
            casted.add( (E) v );
        }
        return casted;
    }
}
