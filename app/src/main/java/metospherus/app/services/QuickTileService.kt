package metospherus.app.services

import android.content.Intent
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import metospherus.app.MainActivity
import metospherus.app.R

class QuickTileService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivityAndCollapse(intent)
        qsTile.updateTile()
    }

    private fun updateTileState() {
        val tile = qsTile
        if (tile != null) {
            tile.icon = Icon.createWithResource(this, R.drawable.ic_notifications)
            tile.state = Tile.STATE_ACTIVE
            tile.label = "Pherus"
            tile.updateTile()
        }
    }
}