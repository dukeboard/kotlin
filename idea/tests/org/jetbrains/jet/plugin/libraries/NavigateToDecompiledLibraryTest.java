/*
 * Copyright 2010-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.plugin.libraries;

import com.beust.jcommander.internal.Maps;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.psi.JetDeclaration;
import org.jetbrains.jet.lang.psi.JetFile;
import org.jetbrains.jet.plugin.JdkAndTestLibraryProjectDescriptor;
import org.junit.Assert;

import java.util.Map;

import static org.jetbrains.jet.plugin.libraries.JetDecompiledData.getDecompiledData;

public class NavigateToDecompiledLibraryTest extends AbstractNavigateToLibraryTest {
    private VirtualFile classFile;

    public void testAbstractClass() {
        doTest();
    }

    public void testClassWithAbstractAndOpenMembers() {
        doTest();
    }

    public void testColor() {
        doTest();
    }

    public void testLibrariesPackage() {
        doTest();
    }

    public void testSimpleClass() {
        doTest();
    }

    public void testSimpleTrait() {
        doTest();
    }

    public void testSimpleTraitImpl() {
        doTest();
    }

    public void testWithInnerAndObject() {
        doTest();
    }

    public void testWithTraitClassObject() {
        doTest();
    }

    public void testClassWithConstructor() {
        doTest();
    }

    public void testNamedObject() {
        doTest();
    }

    private void doTest() {
        classFile = getClassFile();
        PsiFile classPsiFile = getPsiManager().findFile(classFile);
        Assert.assertTrue(classPsiFile instanceof JetFile);
        Map<String, JetDeclaration> map = getRenderedDescriptorToKotlinPsiMap((JetFile) classPsiFile,
                                                    getDecompiledData(classFile, getProject()) .getRenderedDescriptorsToRanges());
        for (Map.Entry<String, JetDeclaration> clsToJet : map.entrySet()) {
            assertSame(classPsiFile, clsToJet.getValue().getContainingFile());
        }
        String decompiledTextWithMarks = getDecompiledTextWithMarks(map);

        assertSameLinesWithFile(TEST_DATA_PATH + "/decompiled/" + getTestName(false) + ".kt", decompiledTextWithMarks);
    }

    private String getDecompiledTextWithMarks(Map<String, JetDeclaration> map) {
        String decompiledText = getDecompiledText();

        int[] openings = new int[decompiledText.length() + 1];
        int[] closings = new int[decompiledText.length() + 1];
        for (JetDeclaration jetDeclaration : map.values()) {
            TextRange textRange = jetDeclaration.getTextRange();
            openings[textRange.getStartOffset()]++;
            closings[textRange.getEndOffset()]++;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i <= decompiledText.length(); i++) {
            result.append(StringUtil.repeat("]", closings[i]));
            result.append(StringUtil.repeat("[", openings[i]));
            if (i < decompiledText.length()) {
                result.append(decompiledText.charAt(i));
            }
        }
        return result.toString();
    }

    private String getDecompiledText() {
        Document document = FileDocumentManager.getInstance().getDocument(classFile);
        assertNotNull(document);
        return document.getText();
    }

    @Nullable
    private LibraryOrderEntry findOurTestLibrary() {
        for (OrderEntry orderEntry : ModuleRootManager.getInstance(myModule).getOrderEntries()) {
            if (orderEntry instanceof LibraryOrderEntry) {
                return (LibraryOrderEntry) orderEntry;
            }
        }
        return null;
    }

    private VirtualFile getClassFile() {
        LibraryOrderEntry library = findOurTestLibrary();
        assertNotNull(library);

        VirtualFile packageDir = library.getFiles(OrderRootType.CLASSES)[0].findFileByRelativePath(PACKAGE.replace(".", "/"));
        assertNotNull(packageDir);
        VirtualFile classFile = packageDir.findChild(getTestName(false) + ".class");
        assertNotNull(classFile);
        return classFile;
    }

    @NotNull
    private static Map<String, JetDeclaration> getRenderedDescriptorToKotlinPsiMap(
            @NotNull JetFile file, @NotNull Map<String, TextRange> renderedDescriptorsToRanges
    ) {
        Map<String, JetDeclaration> renderedDescriptorsToJetDeclarations = Maps.newHashMap();
        for (Map.Entry<String, TextRange> renderedDescriptorToRange : renderedDescriptorsToRanges.entrySet()) {
            String renderedDescriptor = renderedDescriptorToRange.getKey();
            TextRange range = renderedDescriptorToRange.getValue();
            JetDeclaration jetDeclaration = PsiTreeUtil.findElementOfClassAtRange(file, range.getStartOffset(), range.getEndOffset(),
                                                                                  JetDeclaration.class);
            assert jetDeclaration != null : "Can't find declaration at " + range + ": "
                                            + file.getText().substring(range.getStartOffset(), range.getEndOffset());
            renderedDescriptorsToJetDeclarations.put(renderedDescriptor, jetDeclaration);
        }
        return renderedDescriptorsToJetDeclarations;
    }

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new JdkAndTestLibraryProjectDescriptor(TEST_DATA_PATH + "/library", true);
    }
}