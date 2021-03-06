package com.nomis.client.application;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.presenter.slots.NestedSlot;
import com.gwtplatform.mvp.client.presenter.slots.SingleSlot;
import com.gwtplatform.mvp.client.proxy.LockInteractionEvent;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.nomis.client.event.LoadingEvent;
import com.nomis.client.event.LoadingHandler;
import com.nomis.client.event.MessageEvent;
import com.nomis.client.event.MessageHandler;
import com.nomis.client.model.Person;
import com.nomis.client.place.NameTokens;
import com.nomis.client.widget.loading.LoadingWidget;
import gwt.material.design.client.ui.MaterialToast;

/**
 * ApplicationPresenter.
 *
 * @author Aliaksei Labotski.
 * @since 4/13/18.
 */
public class ApplicationPresenter extends
    Presenter<ApplicationPresenter.MyView, ApplicationPresenter.MyProxy> implements ApplicationUiHandlers,
    LoadingHandler, MessageHandler {

  @ProxyStandard
  interface MyProxy extends Proxy<ApplicationPresenter> {

  }

  interface MyView extends View, HasUiHandlers<ApplicationUiHandlers> {

  }

  public static final SingleSlot SLOT_LOADING_CONTENT = new SingleSlot();
  public static final NestedSlot SLOT_MAIN_CONTENT = new NestedSlot();

  @Inject
  private Person person;

  @Inject
  private ApplicationConstants applicationConstants;

  @Inject
  private LoadingWidget loadingWidget;

  private final PlaceManager placeManager;

  @Inject
  ApplicationPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManage) {
    super(eventBus, view, proxy, RevealType.Root);
    this.placeManager = placeManage;
    getView().setUiHandlers(this);
  }

  @Override
  protected void onBind() {
    addRegisteredHandler(LoadingEvent.TYPE, this);
    addRegisteredHandler(MessageEvent.TYPE, this);
    super.onBind();
  }

  @ProxyEvent
  public void onLockInteraction(LockInteractionEvent event) {
    LoadingEvent.fire(this, event.shouldLock());
  }

  @Override
  public void logout() {
    person.setAuthorization(false);
    PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.getLogin())
        .build();
    placeManager.revealPlace(placeRequest);
    MessageEvent.fire(this, applicationConstants.logoutSuccess());
  }

  @Override
  public void onLoading(LoadingEvent event) {
    if (event.isShowLoading()) {
      setInSlot(SLOT_LOADING_CONTENT, loadingWidget);
    } else {
      removeFromSlot(SLOT_LOADING_CONTENT, loadingWidget);
    }
  }

  @Override
  public void onMessage(MessageEvent event) {
    MaterialToast.fireToast(event.getMessage());
  }
}
