/*******************************************************************************
 * Copyright (c) 2011 PHP-Maven.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     PHP-Maven.org
 *******************************************************************************/

package org.phpmaven.eclipse.ui.prj;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.WorkbenchJob;
import org.phpmaven.eclipse.core.PhpmavenCorePlugin;
import org.phpmaven.eclipse.core.archetype.IArchetype;

/**
 * Project wizard page (archetype page)
 * 
 * @author mepeisen
 */
public class ProjWizArchetypePage extends WizardPage implements IPrjWizardPage {
    
    /** the filter text. */
    private Text filterText;
    
    /** the body. */
    private Composite body;
    
    /** the refresh job. */
    private WorkbenchJob refreshJob;

    /** the previous filter text. */
    protected String previousFilterText = ""; //$NON-NLS-1$

    /** the filter pattern. */
    protected Pattern filterPattern;

    /** The disposables. */
    private final List<Resource> disposables = new ArrayList<Resource>();

    /** the hand cursor. */
    private Cursor handCursor;
    
    /** */
    private static final String COLOR_WHITE = "white"; //$NON-NLS-1$
    
    /** */
    private static final String COLOR_DARK_GRAY = "DarkGray"; //$NON-NLS-1$
    
    /** */
    private static final String COLOR_CATEGORY_GRADIENT_START = "category.gradient.start"; //$NON-NLS-1$
    
    /** */
    private static final String COLOR_CATEGORY_GRADIENT_END = "category.gradient.end"; //$NON-NLS-1$ 

    /** */
    private Color colorCategoryGradientStart;

    /** */
    private Color colorCategoryGradientEnd;

    /** */
    private Color colorDisabled;

    /** */
    private Color colorWhite;

    /** */
    private Font h2Font;

    /** */
    private Font h1Font;

    /** */
    private ScrolledComposite bodyScrolledComposite;

    /** The last selected item. */
    private ArchetypeDescriptorItemUi lastSelected;

    /**
     * Constructor.
     */
    public ProjWizArchetypePage() {
        super("ArchetypeSelection"); //$NON-NLS-1$
        setPageComplete(false);
        setTitle(Messages.ProjWizArchetypePage_Title);
        setDescription(Messages.ProjWizArchetypePage_Description);
    }

    /** initializes the cursors. */
    private void initializeCursors() {
        if (this.handCursor == null) {
            this.handCursor = new Cursor(getShell().getDisplay(), SWT.CURSOR_HAND);
            this.disposables.add(this.handCursor);
        }
    }

    /** initializes the images. */
    private void initializeImages() {
//        if (infoImage == null) {
//            infoImage = SVNTeamUIPlugin.instance().getImageDescriptor("icons/discovery/message_info.gif").createImage();
//            disposables.add(infoImage);
//        }
    }
    
    /** initializes the colors. */
    private void initializeColors() {
        if (this.colorWhite == null) {
            ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
            if (!colorRegistry.hasValueFor(COLOR_WHITE)) {
                colorRegistry.put(COLOR_WHITE, new RGB(255, 255, 255));
            }
            this.colorWhite = colorRegistry.get(COLOR_WHITE);
        }
        if (this.colorDisabled == null) {
            ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
            if (!colorRegistry.hasValueFor(COLOR_DARK_GRAY)) {
                colorRegistry.put(COLOR_DARK_GRAY, new RGB(0x69, 0x69, 0x69));
            }
            this.colorDisabled = colorRegistry.get(COLOR_DARK_GRAY);
        }
        
        if (this.colorCategoryGradientStart == null) {
            ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
            if (!colorRegistry.hasValueFor(COLOR_CATEGORY_GRADIENT_START)) {
                colorRegistry.put(COLOR_CATEGORY_GRADIENT_START, new RGB(240, 240, 240));
            }
            this.colorCategoryGradientStart = colorRegistry.get(COLOR_CATEGORY_GRADIENT_START);
        }
        
        if (this.colorCategoryGradientEnd == null) {
            ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
            if (!colorRegistry.hasValueFor(COLOR_CATEGORY_GRADIENT_END)) {
                colorRegistry.put(COLOR_CATEGORY_GRADIENT_END, new RGB(220, 220, 220));
            }
            this.colorCategoryGradientEnd = colorRegistry.get(COLOR_CATEGORY_GRADIENT_END);
        }       
    }

    /** initializes the fonts. */
    private void initializeFonts() {
        // create a level-2 heading font
        if (this.h2Font == null) {
            Font baseFont = JFaceResources.getDialogFont();
            FontData[] fontData = baseFont.getFontData();
            for (FontData data : fontData) {
                data.setStyle(data.getStyle() | SWT.BOLD);
                data.height = data.height * 1.25f;
            }
            this.h2Font = new Font(Display.getCurrent(), fontData);
            this.disposables.add(this.h2Font);
        }
        // create a level-1 heading font
        if (this.h1Font == null) {
            Font baseFont = JFaceResources.getDialogFont();
            FontData[] fontData = baseFont.getFontData();
            for (FontData data : fontData) {
                data.setStyle(data.getStyle() | SWT.BOLD);
                data.height = data.height * 1.35f;
            }
            this.h1Font = new Font(Display.getCurrent(), fontData);
            this.disposables.add(this.h1Font);
        }
    }

    @Override
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        createRefreshJob();
        
        final Composite container = new Composite(parent, SWT.NULL);
        container.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                ProjWizArchetypePage.this.refreshJob.cancel();
            }
        });  

        container.setLayout(new GridLayout(1, false));
        
        // mostly taken from http://dev.eclipse.org/viewcvs/viewvc.cgi/trunk/org.eclipse.team.svn.ui/src/org/eclipse/team/svn/ui/discovery/wizards/ConnectorDiscoveryWizardMainPage.java?view=co&root=Technology_SUBVERSIVE 
        { // header
            final Composite header = new Composite(container, SWT.NULL);
            GridLayoutFactory.fillDefaults().applyTo(header);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(header);

            Composite filterContainer = new Composite(header, SWT.NULL);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(filterContainer);

            int numColumns = 2; // 1 for label, 2 for text filter
            GridLayoutFactory.fillDefaults().numColumns(numColumns).applyTo(filterContainer);
            final Label label = new Label(filterContainer, SWT.NULL);
            label.setText(Messages.ProjWizArchetypePage_LabelFind);

            final Composite textFilterContainer = new Composite(filterContainer, SWT.NULL);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(textFilterContainer);
            GridLayoutFactory.fillDefaults().numColumns(2).applyTo(textFilterContainer);

            this.filterText = new Text(textFilterContainer, SWT.SINGLE | SWT.BORDER | SWT.SEARCH
                    | SWT.ICON_CANCEL);

            this.filterText.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    filterTextChanged();
                }
            });
            
            this.filterText.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    if (e.detail == SWT.ICON_CANCEL) {
                        clearFilterText();
                    }
                }
            });
            GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(this.filterText);
        }
        
        { // container
            this.body = new Composite(container, SWT.NULL);
            GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 480).applyTo(this.body);
        }
        Dialog.applyDialogFont(container);
        setControl(container);
        
        createBodyContents();
    }

    /**
     * Creates the refresh job.
     */
    private void createRefreshJob() {
        this.refreshJob = new WorkbenchJob("filter") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                if (ProjWizArchetypePage.this.filterText.isDisposed()) {
                    return Status.CANCEL_STATUS;
                }
                
                String text = ProjWizArchetypePage.this.filterText.getText();
                text = text.trim();
                
                if (!ProjWizArchetypePage.this.previousFilterText .equals(text)) {
                    ProjWizArchetypePage.this.previousFilterText = text;
                    ProjWizArchetypePage.this.filterPattern = createPattern(ProjWizArchetypePage.this.previousFilterText);
                    createBodyContents();
                }
                
                return Status.OK_STATUS;
                
            }
        };
        this.refreshJob.setSystem(true);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        for (Resource resource : this.disposables) {
            resource.dispose();
        }
        clearDisposables();
    }
    
    /**
     * Clears the disposables.
     */
    private void clearDisposables() {
        this.disposables.clear();
        this.h1Font = null;
        this.h2Font = null;
        // infoImage = null;
        this.handCursor = null;
        this.colorCategoryGradientStart = null;
        this.colorCategoryGradientEnd = null;
    }

    /**
     * Creates the body content (the archetype list).
     */
    protected void createBodyContents() {
     // remove any existing contents
        for (Control child : this.body.getChildren()) {
            child.dispose();
        }
        this.clearDisposables();
        initializeCursors();
        initializeImages();
        initializeFonts();
        initializeColors();

        GridLayoutFactory.fillDefaults().applyTo(this.body);

        this.bodyScrolledComposite = new ScrolledComposite(this.body, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

        configureLook(this.bodyScrolledComposite, this.colorWhite);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this.bodyScrolledComposite);

        final Composite scrolledContentsComposite = new Composite(this.bodyScrolledComposite, SWT.NONE);
        configureLook(scrolledContentsComposite, this.colorWhite);
        scrolledContentsComposite.setRedraw(false);
        try {
            createArchetypesContents(scrolledContentsComposite);
        } finally {
            scrolledContentsComposite.layout(true);
            scrolledContentsComposite.setRedraw(true);
        }
        Point size = scrolledContentsComposite.computeSize(this.body.getSize().x, SWT.DEFAULT, true);
        scrolledContentsComposite.setSize(size);

        this.bodyScrolledComposite.setExpandHorizontal(true);
        this.bodyScrolledComposite.setMinWidth(100);
        this.bodyScrolledComposite.setExpandVertical(true);
        this.bodyScrolledComposite.setMinHeight(1);

        this.bodyScrolledComposite.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                // XXX small offset in case list has a scroll bar
                Point sz = scrolledContentsComposite.computeSize(ProjWizArchetypePage.this.body.getSize().x - 20, SWT.DEFAULT, true);
                scrolledContentsComposite.setSize(sz);
                ProjWizArchetypePage.this.bodyScrolledComposite.setMinHeight(sz.y);
            }
        });

        this.bodyScrolledComposite.setContent(scrolledContentsComposite);

        Dialog.applyDialogFont(this.body);
        // we've changed it so it needs to know
        this.body.layout(true);
    }
    
    /**
     * Creates the archetypes content.
     * @param container container
     */
    private void createArchetypesContents(Composite container) {
        final Color background = container.getBackground();
        GridLayoutFactory.fillDefaults().numColumns(2).spacing(0, 0).applyTo(container);
        
        final Iterable<IArchetype> archetypes = PhpmavenCorePlugin.getArchetypeRegistry().getArchetypes();
        // TODO Sort? Collections.sort(categories, new DiscoveryCategoryComparator());
        
        // for headers/ grouping add the following snippet:
        /*
         *                 { // category header
                    final GradientCanvas categoryHeaderContainer = new GradientCanvas(container, SWT.NONE);
                    categoryHeaderContainer.setSeparatorVisible(true);
                    categoryHeaderContainer.setSeparatorAlignment(SWT.TOP);
                    categoryHeaderContainer.setBackgroundGradient(new Color[] { colorCategoryGradientStart,
                            colorCategoryGradientEnd }, new int[] { 100 }, true);
                    categoryHeaderContainer.putColor(IFormColors.H_BOTTOM_KEYLINE1, colorCategoryGradientStart);
                    categoryHeaderContainer.putColor(IFormColors.H_BOTTOM_KEYLINE2, colorCategoryGradientEnd);

                    GridDataFactory.fillDefaults().span(2, 1).applyTo(categoryHeaderContainer);
                    GridLayoutFactory.fillDefaults().numColumns(3).margins(5, 5).equalWidth(false).applyTo(
                            categoryHeaderContainer);

                    Label iconLabel = new Label(categoryHeaderContainer, SWT.NULL);
                    if (category.getIcon() != null) {
                        Image image = computeIconImage(category.getSource(), category.getIcon(), 48, true);
                        if (image != null) {
                            iconLabel.setImage(image);
                        }
                    }
                    iconLabel.setBackground(null);
                    GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.BEGINNING).span(1, 2).applyTo(iconLabel);

                    Label nameLabel = new Label(categoryHeaderContainer, SWT.NULL);
                    nameLabel.setFont(h1Font);
                    nameLabel.setText(category.getName());
                    nameLabel.setBackground(null);

                    GridDataFactory.fillDefaults().grab(true, false).applyTo(nameLabel);
                    if (hasTooltip(category)) {
                        ToolBar toolBar = new ToolBar(categoryHeaderContainer, SWT.FLAT);
                        toolBar.setBackground(null);
                        ToolItem infoButton = new ToolItem(toolBar, SWT.PUSH);
                        infoButton.setImage(infoImage);
                        infoButton.setToolTipText(SVNUIMessages.ConnectorDiscoveryWizardMainPage_tooltip_showOverview);
                        hookTooltip(toolBar, infoButton, categoryHeaderContainer, nameLabel, category.getSource(),
                                category.getOverview());
                        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(toolBar);
                    } else {
                        new Label(categoryHeaderContainer, SWT.NULL).setText(" "); //$NON-NLS-1$
                    }
                    Label description = new Label(categoryHeaderContainer, SWT.WRAP);
                    GridDataFactory.fillDefaults().grab(true, false).span(2, 1).hint(100, SWT.DEFAULT).applyTo(
                            description);
                    description.setBackground(null);
                    description.setText(category.getDescription());
                }
                categoryChildrenContainer = new Composite(container, SWT.NULL);
                configureLook(categoryChildrenContainer, background);
                GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(categoryChildrenContainer);
                GridLayoutFactory.fillDefaults().spacing(0, 0).applyTo(categoryChildrenContainer);

         */
        int numChildren = 0;
        final Composite categoryChildrenContainer = new Composite(container, SWT.NULL);
        configureLook(categoryChildrenContainer, background);
        GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(categoryChildrenContainer);
        GridLayoutFactory.fillDefaults().spacing(0, 0).applyTo(categoryChildrenContainer);
        for (final IArchetype at : archetypes) {
            if (isFiltered(at)) {
                continue;
            }

            if (++numChildren > 1) {
                // a separator between connector descriptors
                Composite border = new Composite(categoryChildrenContainer, SWT.NULL);
                GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 1).applyTo(border);
                GridLayoutFactory.fillDefaults().applyTo(border);
                border.addPaintListener(new ConnectorBorderPaintListener());
            }

            ArchetypeDescriptorItemUi itemUi = new ArchetypeDescriptorItemUi(at,
                    categoryChildrenContainer, background);
            itemUi.updateAvailability();
        }

        // last one gets a border
        Composite border = new Composite(categoryChildrenContainer, SWT.NULL);
        GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 1).applyTo(border);
        GridLayoutFactory.fillDefaults().applyTo(border);
        border.addPaintListener(new ConnectorBorderPaintListener());

        container.layout(true);
        container.redraw();
    }
    
    /**
     * configures look/ background.
     * @param control the control
     * @param background the background
     */
    private void configureLook(Control control, Color background) {
        control.setBackground(background);
    }

    /**
     * Creates the filter pattern.
     * @param filter filter string
     * @return pattern
     */
    protected Pattern createPattern(String filter) {
        if (filter == null || filter.length() == 0) {
            return null;
        }
        final String regex = filter.replace("\\", "\\\\").replace("?", ".").replace("*", ".*?"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    }

    /**
     * Clears the filter text.
     */
    protected void clearFilterText() {
        this.filterText.setText(""); //$NON-NLS-1$
        filterTextChanged();
    }

    /**
     * Filters the body.
     */
    protected void filterTextChanged() {
        this.refreshJob.cancel();
        this.refreshJob.schedule(200L);
    }

//    public boolean isExistingLocation() {
//        return false;
//    }
    
    /**
     * Returns the selected archetype.
     * @return archetype.
     */
    public IArchetype getSelectedArchetype() {
        return this.lastSelected == null ? null : this.lastSelected.archetype;
    }

    /**
     * Creates a project resource handle for the current project name field
     * value.
     * <p>
     * This method does not create the project resource; this is the
     * responsibility of <code>IProject::create</code> invoked by the new
     * project resource wizard.
     * </p>
     * 
     * @return the new project resource handle
     */
    @Override
    public IProject getProjectHandle() {
        /*return ResourcesPlugin.getWorkspace().getRoot()
                .getProject(fNameGroup.getName());*/
        return null;
    }
    
    /**
     * @param archetype
     * @return b
     */
    private boolean isFiltered(IArchetype archetype) {
        if (this.filterPattern != null) {
            if (!(filterMatches(archetype.getName()) || filterMatches(archetype.getDescription())
                    || filterMatches(archetype.getGroupId()) || filterMatches(archetype.getArtifactId()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param text
     * @return b
     */
    private boolean filterMatches(String text) {
        return text != null && this.filterPattern.matcher(text).find();
    }
    
    /** */
    public class ConnectorBorderPaintListener implements PaintListener {
        @Override
        public void paintControl(PaintEvent e) {
            Composite composite = (Composite) e.widget;
            Rectangle bounds = composite.getBounds();
            GC gc = e.gc;
            gc.setLineStyle(SWT.LINE_DOT);
            gc.drawLine(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y);
        }
    }
    
    /** */
    private class ArchetypeDescriptorItemUi implements PropertyChangeListener, Runnable {
        /** */
        private final IArchetype archetype;

        /** */
        private final Button checkbox;

        /** */
        private final Label iconLabel;

        /** */
        private final Label nameLabel;

//        /** */
//        private ToolItem infoButton;

        /** */
        private final Label providerLabel;

        /** */
        private final Label description;

        /** */
        private final Composite checkboxContainer;

        /** */
        private final Composite connectorContainer;

        /** */
        private final Display display;

//        private Image iconImage;

//        /** */
//        private Image warningIconImage;

        /**
         * Constructor.
         * @param archetype
         * @param categoryChildrenContainer
         * @param background
         */
        public ArchetypeDescriptorItemUi(IArchetype archetype, Composite categoryChildrenContainer,
                Color background) {
            this.display = categoryChildrenContainer.getDisplay();
            this.archetype = archetype;
            // TODO archetype.addPropertyChangeListener(this);

            this.connectorContainer = new Composite(categoryChildrenContainer, SWT.NULL);

            configureLook(this.connectorContainer, background);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(this.connectorContainer);
            GridLayout layout = new GridLayout(4, false);
            layout.marginLeft = 7;
            layout.marginTop = 2;
            layout.marginBottom = 2;
            this.connectorContainer.setLayout(layout);

            this.checkboxContainer = new Composite(this.connectorContainer, SWT.NULL);
            configureLook(this.checkboxContainer, background);
            GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.BEGINNING).span(1, 2).applyTo(this.checkboxContainer);
            GridLayoutFactory.fillDefaults().spacing(1, 1).numColumns(2).applyTo(this.checkboxContainer);

            this.checkbox = new Button(this.checkboxContainer, SWT.RADIO);
            this.checkbox.setText(" "); //$NON-NLS-1$
            // help UI tests
            this.checkbox.setData("connectorId", archetype); //$NON-NLS-1$
            configureLook(this.checkbox, background);
            this.checkbox.setSelection(false);
            this.checkbox.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    ProjWizArchetypePage.this.bodyScrolledComposite.showControl(ArchetypeDescriptorItemUi.this.connectorContainer);
                }
            });

            GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(this.checkbox);

            this.iconLabel = new Label(this.checkboxContainer, SWT.NULL);
            configureLook(this.iconLabel, background);
            GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(this.iconLabel);

//            if (connector.getIcon() != null) {
//                iconImage = computeIconImage(connector.getSource(), connector.getIcon(), 32, false);
//                if (iconImage != null) {
//                    iconLabel.setImage(iconImage);
//                }
//            }

            this.nameLabel = new Label(this.connectorContainer, SWT.NULL);
            configureLook(this.nameLabel, background);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(this.nameLabel);
            this.nameLabel.setFont(ProjWizArchetypePage.this.h2Font);
            this.nameLabel.setText(this.archetype.getName());

            this.providerLabel = new Label(this.connectorContainer, SWT.NULL);
            configureLook(this.providerLabel, background);
            GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(this.providerLabel);
            // TODO
//            this.providerLabel.setText(SVNUIMessages.format(SVNUIMessages.ConnectorDiscoveryWizardMainPage_provider_and_license,
//                    new Object[]{connector.getProvider(), connector.getLicense()}));

//            if (hasTooltip(connector)) {
//                ToolBar toolBar = new ToolBar(connectorContainer, SWT.FLAT);
//                toolBar.setBackground(background);
//
//                infoButton = new ToolItem(toolBar, SWT.PUSH);
//                infoButton.setImage(infoImage);
//                infoButton.setToolTipText(SVNUIMessages.ConnectorDiscoveryWizardMainPage_tooltip_showOverview);
//                hookTooltip(toolBar, infoButton, connectorContainer, nameLabel, connector.getSource(),
//                        connector.getOverview());
//                GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(toolBar);
//            } else {
                new Label(this.connectorContainer, SWT.NULL).setText(" "); //$NON-NLS-1$
//            }

            this.description = new Label(this.connectorContainer, SWT.NULL | SWT.WRAP);
            configureLook(this.description, background);

            GridDataFactory.fillDefaults().grab(true, false).span(3, 1).hint(100, SWT.DEFAULT).applyTo(this.description);
            String descriptionText = this.archetype.getDescription();
            int maxDescriptionLength = 162;
            if (descriptionText.length() > maxDescriptionLength) {
                descriptionText = descriptionText.substring(0, maxDescriptionLength);
            }
            this.description.setText(descriptionText.replaceAll("(\\r\\n)|\\n|\\r", " ")); //$NON-NLS-1$ //$NON-NLS-2$

            // always disabled color to make it less prominent
            this.providerLabel.setForeground(ProjWizArchetypePage.this.colorDisabled);

            this.checkbox.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                }

                @Override
                public void widgetSelected(SelectionEvent e) {
                    boolean selected = ArchetypeDescriptorItemUi.this.checkbox.getSelection();
                    maybeModifySelection(selected);
                }
            });
            MouseListener connectorItemMouseListener = new MouseAdapter() {
                @Override
                public void mouseUp(MouseEvent e) {
                    boolean selected = !ArchetypeDescriptorItemUi.this.checkbox.getSelection();
                    if (maybeModifySelection(selected)) {
                        ArchetypeDescriptorItemUi.this.checkbox.setSelection(selected);
                    }
                }
            };
            this.checkboxContainer.addMouseListener(connectorItemMouseListener);
            this.connectorContainer.addMouseListener(connectorItemMouseListener);
            this.iconLabel.addMouseListener(connectorItemMouseListener);
            this.nameLabel.addMouseListener(connectorItemMouseListener);
            this.providerLabel.addMouseListener(connectorItemMouseListener);
            this.description.addMouseListener(connectorItemMouseListener);
        }

        /**
         * Tests if the selected checkboy can be set.
         * @param selected selected checkbox
         * @return true if the modification can be set.
         */
        protected boolean maybeModifySelection(boolean selected) {
//            if (selected) {
//                if (connector.getAvailable() == null) {
//                    return false;
//                }
//                if (!connector.getAvailable()) {
//                    MessageDialog.openWarning(getShell(),
//                            SVNUIMessages.ConnectorDiscoveryWizardMainPage_warningTitleConnectorUnavailable, SVNUIMessages.format(
//                                    SVNUIMessages.ConnectorDiscoveryWizardMainPage_warningMessageConnectorUnavailable,
//                                    connector.getName()));
//                    return false;
//                }
//            }
            ProjWizArchetypePage.this.modifySelection(this, selected);
            return true;
        }

        /**
         * Updates the availability.
         */
        public void updateAvailability() {
            boolean enabled = /*connector.getAvailable() != null && connector.getAvailable()*/ true;

            this.checkbox.setEnabled(enabled);
            this.nameLabel.setEnabled(enabled);
            this.providerLabel.setEnabled(enabled);
            this.description.setEnabled(enabled);
            Color foreground;
            if (enabled) {
                foreground = this.connectorContainer.getForeground();
            } else {
                foreground = ProjWizArchetypePage.this.colorDisabled;
            }
            this.nameLabel.setForeground(foreground);
            this.description.setForeground(foreground);

//            if (iconImage != null) {
//                boolean unavailable = !enabled && connector.getAvailable() != null;
//                if (unavailable) {
//                    if (warningIconImage == null) {
//                        warningIconImage = new DecorationOverlayIcon(iconImage, SVNTeamUIPlugin.instance().getImageDescriptor("icons/discovery/message_warning.gif"),
//                                IDecoration.TOP_LEFT).createImage();
//                        disposables.add(warningIconImage);
//                    }
//                    iconLabel.setImage(warningIconImage);
//                } else if (warningIconImage != null) {
//                    iconLabel.setImage(iconImage);
//                }
//            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            this.display.asyncExec(this);
        }

        @Override
        public void run() {
            if (!this.connectorContainer.isDisposed()) {
                updateAvailability();
            }
        }
    }

    /**
     * @param archetype
     * @param selected
     */
    public void modifySelection(ArchetypeDescriptorItemUi archetype, boolean selected) {
        if (selected && this.lastSelected != null && archetype != this.lastSelected) {
            this.lastSelected.checkbox.setSelection(false);
        }
        this.lastSelected = selected ? archetype : null;
        this.setPageComplete(this.lastSelected != null);
    }
    
}
