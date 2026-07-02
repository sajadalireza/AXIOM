package com.axiom.app.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class SystemFeedRepositoryImpl_Factory implements Factory<SystemFeedRepositoryImpl> {
  @Override
  public SystemFeedRepositoryImpl get() {
    return newInstance();
  }

  public static SystemFeedRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SystemFeedRepositoryImpl newInstance() {
    return new SystemFeedRepositoryImpl();
  }

  private static final class InstanceHolder {
    static final SystemFeedRepositoryImpl_Factory INSTANCE = new SystemFeedRepositoryImpl_Factory();
  }
}
