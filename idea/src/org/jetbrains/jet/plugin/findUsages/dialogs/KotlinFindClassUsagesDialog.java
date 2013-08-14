package org.jetbrains.jet.plugin.findUsages.dialogs;

import com.intellij.find.FindBundle;
import com.intellij.find.findUsages.FindClassUsagesDialog;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.find.findUsages.JavaClassFindUsagesOptions;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.StateRestoringCheckBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.asJava.KotlinLightClassForExplicitDeclaration;
import org.jetbrains.jet.plugin.JetBundle;
import org.jetbrains.jet.plugin.findUsages.options.KotlinClassFindUsagesOptions;
import org.jetbrains.jet.plugin.refactoring.JetRefactoringUtil;

import javax.swing.*;

public class KotlinFindClassUsagesDialog extends FindClassUsagesDialog {
    private StateRestoringCheckBox constructorUsages;

    public KotlinFindClassUsagesDialog(
            PsiClass klass,
            Project project,
            FindUsagesOptions findUsagesOptions,
            boolean toShowInNewTab,
            boolean mustOpenInNewTab,
            boolean isSingleFile,
            FindUsagesHandler handler
    ) {
        super(klass, project, findUsagesOptions, toShowInNewTab, mustOpenInNewTab, isSingleFile, handler);
    }

    @Override
    protected JPanel createFindWhatPanel() {
        JPanel findWhatPanel = super.createFindWhatPanel();
        assert findWhatPanel != null;

        Utils.renameCheckbox(
                findWhatPanel,
                FindBundle.message("find.what.methods.usages.checkbox"),
                JetBundle.message("find.what.functions.usages.checkbox")
        );
        Utils.renameCheckbox(
                findWhatPanel,
                FindBundle.message("find.what.fields.usages.checkbox"),
                JetBundle.message("find.what.properties.usages.checkbox")
        );
        Utils.renameCheckbox(
                findWhatPanel,
                FindBundle.message("find.what.implementing.classes.checkbox"),
                JetBundle.message("find.what.implementing.classes.checkbox")
        );
        Utils.renameCheckbox(
                findWhatPanel,
                FindBundle.message("find.what.derived.interfaces.checkbox"),
                JetBundle.message("find.what.derived.traits.checkbox")
        );
        Utils.renameCheckbox(
                findWhatPanel,
                FindBundle.message("find.what.derived.classes.checkbox"),
                JetBundle.message("find.what.derived.classes.checkbox")
        );

        constructorUsages = addCheckboxToPanel(
                JetBundle.message("find.what.constructor.usages.checkbox"),
                getFindUsagesOptions().searchConstructorUsages,
                findWhatPanel,
                true
        );

        return findWhatPanel;
    }

    @Override
    protected KotlinClassFindUsagesOptions getFindUsagesOptions() {
        return (KotlinClassFindUsagesOptions) super.getFindUsagesOptions();
    }

    @Override
    public void configureLabelComponent(@NotNull SimpleColoredComponent coloredComponent) {
        PsiClass klass = (PsiClass) getPsiElement();
        if (klass instanceof KotlinLightClassForExplicitDeclaration) {
            coloredComponent.append(JetRefactoringUtil.formatClass(((KotlinLightClassForExplicitDeclaration) klass).getJetClassOrObject()));
        }
    }

    @Override
    protected void update() {
        super.update();
        if (!isOKActionEnabled() && constructorUsages.isSelected()) {
            setOKActionEnabled(true);
        }
    }

    @Override
    public void calcFindUsagesOptions(JavaClassFindUsagesOptions options) {
        assert options instanceof KotlinClassFindUsagesOptions;

        super.calcFindUsagesOptions(options);
        ((KotlinClassFindUsagesOptions) options).searchConstructorUsages = constructorUsages.isSelected();
    }
}
