package gameObservableControllers;

import gameObservables.Bascule;
import gameObservables.StickBascule;
import javafx.scene.input.MouseEvent;

public class StickBasculeController extends BasculeController {

	private StickBascule bascule;

	@Override
	protected void notifyPressed(MouseEvent event) {
		super.notifyPressed(event);
		bascule.dropCoin();
	}

	@Override
	public void setBascule(Bascule bascule) {
		if (bascule instanceof StickBascule) {
			super.setBascule(bascule);
		}
		this.bascule = (StickBascule) bascule;

	}

}
