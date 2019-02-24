package com.chase.dps.plugin.inspections;

@FunctionalInterface
interface TriPredicate <T, U, S> {

     boolean test(T t, U u, S s);

}
