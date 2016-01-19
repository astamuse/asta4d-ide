package com.astamuse.asta4d.ide.eclipse.property;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

public class Asta4dProjectPropertyPage extends PropertyPage {

    private Asta4dPreference pref;

    private Asta4dProperties unChangedProperties;

    private Asta4dProperties editingProperties;

    private Composite comp;

    private Text prefixText;

    private Text namespaceText;

    public Asta4dProjectPropertyPage() {
        super();
        noDefaultButton();
    }

    @Override
    public void setElement(IAdaptable element) {
        super.setElement(element);
        pref = Asta4dPreference.get((IProject) element);
    }

    @Override
    protected Control createContents(Composite parent) {
        // define the comp to fill whole space of parent
        comp = new Composite(parent, NONE);
        comp.setLayoutData(new GridData(GridData.FILL_BOTH));

        // define a top to down simple layout
        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 0;
        comp.setLayout(layout);

        createTemplateInfoEditor(comp);

        createSnippetInfoEditor(comp);

        loadPrefs();
        registerUpdateListeners();

        return comp;
    }

    @Override
    public boolean okToLeave() {
        boolean ok = super.okToLeave();
        if (!ok) {
            return false;
        }

        if (editingProperties.equals(unChangedProperties)) {
            return true;
        }

        String message = "There are unsaved changes, OK to save and Cancel to discard";
        boolean doSave = MessageDialog.openConfirm(comp.getShell(), "Confirm change", message);
        if (doSave) {
            storePrefs();
        } else {
            loadPrefs();
        }
        return true;
    }

    private void createTemplateInfoEditor(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText("Template");
        group.setLayoutData(createHFillGridData());

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);

        Label label = new Label(group, NONE);
        label.setLayoutData(new GridData(GridData.FILL));
        label.setText("tag namespace:");

        namespaceText = new Text(group, SWT.SINGLE | SWT.BORDER);
        GridData griddata = new GridData(GridData.FILL_HORIZONTAL);
        namespaceText.setLayoutData(griddata);
    }

    private void createSnippetInfoEditor(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText("Snippet");
        group.setLayoutData(createHFillGridData());

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        group.setLayout(layout);

        Label label = new Label(group, NONE);
        label.setLayoutData(createHFillGridData());
        label.setText("Prefixes for snippet class search, split by row, ending with dot if necessary:\n");

        prefixText = new Text(group, SWT.MULTI | SWT.BORDER);
        GridData griddata = new GridData(GridData.FILL_BOTH);
        griddata.heightHint = 180;
        prefixText.setLayoutData(griddata);
    }

    @Override
    public boolean performOk() {
        storePrefs();
        return true;
    }

    private GridData createHFillGridData() {
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        return gd;
    }

    private void registerUpdateListeners() {
        prefixText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String s = prefixText.getText().trim();
                s = StringUtils.replace(s, "\r", "");
                editingProperties.setSnippetPrefixes(s.split("\n"));
            }
        });
        namespaceText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                editingProperties.setNamespace(namespaceText.getText().trim());
            }
        });
    }

    private void loadPrefs() {
        unChangedProperties = this.pref.loadProperties();
        editingProperties = unChangedProperties.clone();
        prefixText.setText(StringUtils.join(editingProperties.getSnippetPrefixes(), "\n"));
        namespaceText.setText(editingProperties.getNamespace());
    }

    private void storePrefs() {
        this.pref.storeProperties(editingProperties);
        unChangedProperties = editingProperties.clone();
    }

}
