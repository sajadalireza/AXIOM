package com.axiom.app.core;

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
public final class XionEventBus_Factory implements Factory<XionEventBus> {
  @Override
  public XionEventBus get() {
    return newInstance();
  }

  public static XionEventBus_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static XionEventBus newInstance() {
    return new XionEventBus();
  }

  private static final class InstanceHolder {
    static final XionEventBus_Factory INSTANCE = new XionEventBus_Factory();
  }
}
