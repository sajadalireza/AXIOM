package com.axiom.app.di;

import android.content.Context;
import com.axiom.app.data.local.AxiomDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppModule_Companion_ProvideAxiomDatabaseFactory implements Factory<AxiomDatabase> {
  private final Provider<Context> contextProvider;

  private AppModule_Companion_ProvideAxiomDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AxiomDatabase get() {
    return provideAxiomDatabase(contextProvider.get());
  }

  public static AppModule_Companion_ProvideAxiomDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new AppModule_Companion_ProvideAxiomDatabaseFactory(contextProvider);
  }

  public static AxiomDatabase provideAxiomDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.Companion.provideAxiomDatabase(context));
  }
}
