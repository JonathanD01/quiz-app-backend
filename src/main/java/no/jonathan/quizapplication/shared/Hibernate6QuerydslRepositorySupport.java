package no.jonathan.quizapplication.shared;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.AbstractJPAQuery;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.lang.NonNull;

/**
 * SOURCE https://github.com/querydsl/querydsl/issues/3428#issuecomment-1710051917
 *
 * <p>Extension of QuerydslRepositorySupport which uses JPQLTemplates.DEFAULT instead of the
 * Hibernate variant https://github.com/querydsl/querydsl/issues/3428
 */
public abstract class Hibernate6QuerydslRepositorySupport extends QuerydslRepositorySupport {

  private Hibernate6Querydsl querydsl;

  public Hibernate6QuerydslRepositorySupport(Class<?> domainClass) {
    super(domainClass);
  }

  @Override
  protected Querydsl getQuerydsl() {
    if (null == querydsl && getEntityManager() != null) {
      this.querydsl = new Hibernate6Querydsl(getEntityManager(), getBuilder());
    }
    return this.querydsl;
  }

  @NonNull
  private Querydsl getRequiredQuerydsl() {
    if (getQuerydsl() == null) {
      throw new IllegalStateException("Querydsl is null");
    }
    return getQuerydsl();
  }

  @Override
  protected JPQLQuery<Object> from(EntityPath<?>... paths) {
    return getRequiredQuerydsl().createQuery(paths);
  }

  @Override
  protected <T> JPQLQuery<T> from(EntityPath<T> path) {
    return getRequiredQuerydsl().createQuery(path).select(path);
  }

  class Hibernate6Querydsl extends Querydsl {
    private final EntityManager entityManager;

    public Hibernate6Querydsl(EntityManager entityManager, PathBuilder<?> builder) {
      super(entityManager, builder);
      this.entityManager = entityManager;
    }

    @Override
    public <T> AbstractJPAQuery<T, JPAQuery<T>> createQuery() {
      // https://github.com/querydsl/querydsl/issues/3428
      return new JPAQuery<>(entityManager, JPQLTemplates.DEFAULT);
    }
  }
}
