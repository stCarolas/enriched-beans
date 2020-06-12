package  com.github.stcarolas.enrichedbeans.processor;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CraftedTypeMirror implements TypeMirror {

  private TypeKind kind;
  private List<AnnotationMirror> annotations;

  @Override
	public <A extends Annotation> A getAnnotation(Class<A> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<? extends AnnotationMirror> getAnnotationMirrors() {
    return annotations;
	}

	@Override
	public <A extends Annotation> A[] getAnnotationsByType(Class<A> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <R, P> R accept(TypeVisitor<R, P> arg0, P arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TypeKind getKind() {
		return kind;
	}

}
