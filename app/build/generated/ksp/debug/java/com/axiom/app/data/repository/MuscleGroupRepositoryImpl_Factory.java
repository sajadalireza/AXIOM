package com.axiom.app.data.repository;

import com.axiom.app.data.local.dao.MuscleGroupDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
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
public final class MuscleGroupRepositoryImpl_Factory implements Factory<MuscleGroupRepositoryImpl> {
  private final Provider<MuscleGroupDao> muscleGroupDaoProvider;

  private MuscleGroupRepositoryImpl_Factory(Provider<MuscleGroupDao> muscleGroupDaoProvider) {
    this.muscleGroupDaoProvider = muscleGroupDaoProvider;
  }

  @Override
  public MuscleGroupRepositoryImpl get() {
    return newInstance(muscleGroupDaoProvider.get());
  }

  public static MuscleGroupRepositoryImpl_Factory create(
      Provider<MuscleGroupDao> muscleGroupDaoProvider) {
    return new MuscleGroupRepositoryImpl_Factory(muscleGroupDaoProvider);
  }

  public static MuscleGroupRepositoryImpl newInstance(MuscleGroupDao muscleGroupDao) {
    return new MuscleGroupRepositoryImpl(muscleGroupDao);
  }
}
