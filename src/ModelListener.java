/**
 * @author Trinh Nguyen
 *
 */
public interface ModelListener {
	public void modelChanged(DShapeModel model);
	public void modelDeleted(DShapeModel model);
	public void modelAdded();
}
