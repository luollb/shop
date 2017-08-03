package cn.cy.mobilegames.yyjia.forelders.util;

public interface DownloadSelectListener {

	public void onDownloadSelectionChanged(long downloadId, boolean isChecked);

	public boolean isDownloadSelected(long id);
}
