package com.freemarker.lpex.formdialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.freemarker.lpex.utils.PluginLogger;
import com.freemarker.lpex.utils.StackTraceUtil;

public class TemplateFormDialog extends FormDialog {

    public TemplateFormDialog(Shell shell) {
            super(shell);
    }

	protected void createFormContent(IManagedForm managedForm) {
		try {
			final ScrolledForm form = managedForm.getForm();
			final FormToolkit toolkit = managedForm.getToolkit();
			/*GridLayout layout = new GridLayout();
			form.getBody().setLayout(layout);

			Hyperlink link = toolkit.createHyperlink(form.getBody(), "Click here.", SWT.WRAP);
			link.addHyperlinkListener(new HyperlinkAdapter() {
				public void linkActivated(HyperlinkEvent e) {
					System.out.println("Link activated!");
				}
			});
			
			layout.numColumns = 2;
			GridData gd = new GridData();
			gd.horizontalSpan = 2;
			link.setLayoutData(gd);
			Label label = new Label(form.getBody(), SWT.NULL);
			label.setText("Text field label:");
			Text text = new Text(form.getBody(), SWT.BORDER);
			text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			Button button = new Button(form.getBody(), SWT.CHECK);
			button.setText("An example of a checkbox in a form");
			gd = new GridData();
			gd.horizontalSpan = 2;
			button.setLayoutData(gd);
			*/
		} catch (Exception e) {
			PluginLogger.logger.info(StackTraceUtil.getStackTrace(e));
		}
	}
}
