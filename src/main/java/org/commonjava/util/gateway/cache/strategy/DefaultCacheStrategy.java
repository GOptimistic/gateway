/**
 * Copyright (C) 2020 John Casey (jdcasey@commonjava.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.util.gateway.cache.strategy;

import org.commonjava.util.gateway.cache.CacheStrategy;
import org.commonjava.util.gateway.config.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Alternative;
import java.io.File;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.commonjava.util.gateway.config.ProxyConfiguration.USER_DIR;

@Alternative
public class DefaultCacheStrategy
                implements CacheStrategy
{
    public static final CacheStrategy INSTANCE = new DefaultCacheStrategy();

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    public static final File DEFAULT_CACHE_DIR = new File( USER_DIR, "cache" );

    @Override
    public boolean isCache( CacheConfiguration cache, String path )
    {
        if ( cache != null && cache.enabled && matchPattern( cache.getCompiledPattern(), path ) )
        {
            logger.trace( "Cache matches (read): {}", path );
            return true;
        }
        return false;
    }

    @Override
    public boolean isCacheForWrite( CacheConfiguration cache, String path )
    {
        if ( cache != null && cache.enabled && !cache.readonly && matchPattern( cache.getCompiledPattern(), path ) )
        {
            logger.trace( "Cache matches (write): {}", path );
            return true;
        }
        return false;
    }

    protected boolean matchPattern( Pattern pattern, String path )
    {
        return pattern == null || pattern.matcher( path ).matches();
    }

    @Override
    public File getCachedFile( CacheConfiguration cache, String path )
    {
        File f;
        if ( isNotBlank( cache.dir ) )
        {
            f = new File( cache.dir, path );
        }
        else
        {
            f = new File( DEFAULT_CACHE_DIR, path );
        }
        return f;
    }

}
