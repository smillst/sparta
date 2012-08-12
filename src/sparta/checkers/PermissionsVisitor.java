package sparta.checkers;

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;

import sparta.checkers.quals.RequiredPermissions;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;

import checkers.basetype.BaseTypeVisitor;
import checkers.source.Result;
import checkers.util.AnnotationUtils;
import checkers.util.TreeUtils;

/**
 * Propagate required permissions up the call stack.
 * Require that they are declared in the manifest.
 *
 * TODO: should we propagate required permissions from (anonymous) inner classes to the outside?
 */
public class PermissionsVisitor extends BaseTypeVisitor<PermissionsChecker> {

    public PermissionsVisitor(PermissionsChecker checker,
            CompilationUnitTree root) {
        super(checker, root);
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
        // Ensure that the enclosing method has at least @RequiredPermissions
        ExecutableElement methodElt = TreeUtils.elementFromUse(node);
        AnnotationMirror reqP = atypeFactory.getDeclAnnotation(methodElt, RequiredPermissions.class);
        if (reqP!=null) {
            List<String> reqPerms = AnnotationUtils.elementValueArray(reqP, "value");
            if (!reqPerms.isEmpty()) {
                ExecutableElement callerElt = TreeUtils.elementFromDeclaration(TreeUtils.enclosingMethod(getCurrentPath()));
                AnnotationMirror callerReq = atypeFactory.getDeclAnnotation(callerElt, RequiredPermissions.class);
                List<String> callerPerms;
                List<String> missing = new LinkedList<String>();
                if (callerReq==null) {
                    missing.addAll(reqPerms);
                    callerPerms = new LinkedList<String>();
                } else {
                    callerPerms = AnnotationUtils.elementValueArray(callerReq, "value");
                    for (String perm : reqPerms) {
                        if (!callerPerms.contains(perm)) {
                            missing.add(perm);
                        }
                    }
                }
                if (!missing.isEmpty()) {
                    checker.report(Result.failure("unsatisfied.permissions", missing, callerPerms), node);
                }
            }
        }
        return super.visitMethodInvocation(node, p);
    }

    @Override
    public Void visitMethod(MethodTree node, Void p) {
        // Ensure that all constants in @RequiredPermissions are in Manifest
        return super.visitMethod(node, p);
    }
}