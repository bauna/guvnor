package org.drools.brms.client.modeldriven.ui;

import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.model.ActionAssertFact;
import org.drools.brms.client.modeldriven.model.ActionRetractFact;
import org.drools.brms.client.modeldriven.model.ActionSetField;
import org.drools.brms.client.modeldriven.model.CompositeFactPattern;
import org.drools.brms.client.modeldriven.model.FactPattern;
import org.drools.brms.client.modeldriven.model.IAction;
import org.drools.brms.client.modeldriven.model.IPattern;
import org.drools.brms.client.modeldriven.model.RuleModel;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the parent widget that contains the model based rule builder.
 * 
 * @author Michael Neale
 *
 */
public class RuleModeller extends Composite {
 
    private FlexTable layout;
    private SuggestionCompletionEngine completions;
    private RuleModel model;
    
    public RuleModeller(SuggestionCompletionEngine com, RuleModel model) {
        this.model = model;
        this.completions = com;
        
        layout = new FlexTable();
        
        refreshWidget();
        
        layout.setStyleName( "model-builder-Background" );
        initWidget( layout );
        
    }

    /**
     * This updates the widget to reflect the state of the model.
     */
    public void refreshWidget() {
        layout.clear();
        
        Image addPattern = new Image( "images/new_item.gif" );
        addPattern.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showFactTypeSelector(w);               
            }            
        });
        
        layout.setWidget( 0, 0, new Label("IF") );
        layout.setWidget( 0, 2, addPattern );
        
        
        
        layout.setWidget( 1, 1, renderLhs(this.model) );
        layout.setWidget( 2, 0, new Label("THEN") );
        layout.setWidget( 3, 1, renderRhs(this.model) );
    }

    /**
     * Do the widgets for the RHS.
     */
    private Widget renderRhs(final RuleModel model) {
        VerticalPanel vert = new VerticalPanel();
        
        for ( int i = 0; i < model.rhs.length; i++ ) {
            IAction action = model.rhs[i];
            
            Widget w = null;            
            if (action instanceof ActionSetField) {                
                w =  new ActionSetFieldWidget(this.model, (ActionSetField) action, completions ) ; 
            } else if (action instanceof ActionAssertFact) {
                w = new ActionAssertFactWidget((ActionAssertFact) action, completions );
            } else if (action instanceof ActionRetractFact) {
                w = new ActionRetractFactWidget((ActionRetractFact) action);
            }
            
            HorizontalPanel horiz = new HorizontalPanel();
            
            Image remove = new Image("images/delete_obj.gif");
            final int idx = i;
            remove.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    model.removeRhsItem(idx);
                    refreshWidget();
                }
            } );
            horiz.add( w );
            horiz.add( remove );
            
            vert.add( horiz );
            
        }
        
        
        
        return vert;
    }

    /**
     * Pops up the fact selector.
     */
    protected void showFactTypeSelector(final Widget w) {
        final ListBox box = new ListBox();
        String[] facts = completions.getFactTypes();
        for ( int i = 0; i < facts.length; i++ ) {
            box.addItem( facts[i] );
        }
        final FormStylePopup popup = new FormStylePopup("images/new_fact.gif", "New fact pattern...");
        popup.addAttribute( "choose type", box );
        Button ok = new Button("OK");
        popup.addAttribute( "", ok );
        
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                addNewFact(box.getItemText( box.getSelectedIndex() ));
                popup.hide();
                
            }
        });
        popup.setStyleName( "ks-popups-Popup" );
        
        popup.setPopupPosition( w.getAbsoluteLeft() - 400, w.getAbsoluteTop() );
        popup.show();
    }

    /**
     * Adds a fact to the model, and then refreshes the display.
     */
    protected void addNewFact(String itemText) {
        IPattern[] list = this.model.lhs;
        IPattern[] newList = new IPattern[list.length + 1];
        
        
        for ( int i = 0; i < list.length; i++ ) {
            newList[i] =  list[i];
        }
        newList[list.length] = new FactPattern(itemText); 
        
        this.model.lhs = newList;
        
        refreshWidget();
    }

    private Widget renderLhs(final RuleModel model) {
        VerticalPanel vert = new VerticalPanel();
        
        for ( int i = 0; i < model.lhs.length; i++ ) {
            IPattern pattern = model.lhs[i];
            Widget w;
            if (pattern instanceof FactPattern) {  
                
                w =  new FactPatternWidget(this, pattern, completions, true) ;
            } else if (pattern instanceof CompositeFactPattern) {
                w = new CompositeFactPatternWidget(this, (CompositeFactPattern) pattern, completions) ;
            } else {
                throw new RuntimeException("I don't know what type of pattern that is.");
            }
            
            HorizontalPanel horiz = new HorizontalPanel();
            
            Image remove = new Image("images/delete_obj.gif");
            final int idx = i;
            remove.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    if (model.removeLhsItem(idx)) {
                        refreshWidget();
                    } else {
                        ErrorPopup.showMessage( "Can't remove that item as it is used in the action part of the rule." );
                    }
                }
            } );
            horiz.add( w );
            horiz.add( remove );


            vert.add( horiz );
        }
        
        return vert;
    }


    
    
    
    
}
