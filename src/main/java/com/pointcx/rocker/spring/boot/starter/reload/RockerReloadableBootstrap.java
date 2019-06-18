package com.pointcx.rocker.spring.boot.starter.reload;

import com.fizzed.rocker.runtime.RockerBootstrap;

public interface RockerReloadableBootstrap extends RockerBootstrap {
    boolean isReloadableClass(String className);
}
