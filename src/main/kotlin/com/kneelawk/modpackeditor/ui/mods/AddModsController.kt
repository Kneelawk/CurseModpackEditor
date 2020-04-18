package com.kneelawk.modpackeditor.ui.mods

import com.kneelawk.modpackeditor.curse.CurseApi
import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.data.SimpleAddonId
import com.kneelawk.modpackeditor.data.curseapi.AddonData
import com.kneelawk.modpackeditor.data.curseapi.CategoryListElementData
import com.kneelawk.modpackeditor.data.version.MinecraftVersion
import com.kneelawk.modpackeditor.ui.ModpackModel
import com.kneelawk.modpackeditor.ui.SelectCategoryFragment
import com.kneelawk.modpackeditor.ui.SelectMinecraftVersionFragment
import com.kneelawk.modpackeditor.ui.util.ModListState
import javafx.collections.FXCollections
import javafx.stage.Modality
import tornadofx.*

/**
 * Created by Kneelawk on 4/12/20.
 */
class AddModsController : Controller() {
    companion object {
        const val PAGE_SIZE: Long = 20
    }

    val model: ModpackModel by inject()
    private val modListState: ModListState by inject()
    private val curseApi: CurseApi by inject()

    val filterByCategory = booleanProperty(false)
    val selectedCategory = objectProperty<CategoryListElementData?>(null)
    val filterByMinecraftVersion = booleanProperty(false)
    val selectedMinecraftVersion = stringProperty(model.minecraftVersion.value)
    val sortBy = objectProperty(TwitchSearchSortBy.POPULARITY)
    val searchFilter = stringProperty("")
    val loadingStatus = objectProperty(AddModsLoadingStatus.NOTHING_LOADED)
    val pageNumber = longProperty(0)
    val loadedAddons = listProperty<AddonData>(FXCollections.observableArrayList())

    fun selectCategory() {
        find<SelectCategoryFragment>(
            SelectCategoryFragment::callback to { result: SelectCategoryFragment.Result ->
                when (result) {
                    is SelectCategoryFragment.Result.Select -> selectedCategory.value = result.category
                    SelectCategoryFragment.Result.Cancel -> {
                    }
                }
            }
        ).openModal(owner = find<AddModsView>().currentWindow)
    }

    fun selectMinecraftVersion() {
        find<SelectMinecraftVersionFragment>(
            SelectMinecraftVersionFragment::previousVersion to MinecraftVersion.tryParse(
                selectedMinecraftVersion.value),
            SelectMinecraftVersionFragment::callback to { result: SelectMinecraftVersionFragment.Result ->
                when (result) {
                    is SelectMinecraftVersionFragment.Result.Cancel -> {
                    }
                    is SelectMinecraftVersionFragment.Result.Select -> {
                        selectedMinecraftVersion.value = result.minecraft.toString()
                    }
                }
            }).openModal(owner = find<AddModsView>().currentWindow)
    }

    fun searchMods() {
        val minecraftVersion = if (filterByMinecraftVersion.value) selectedMinecraftVersion.value else null
        val categoryId = if (filterByCategory.value) selectedCategory.value?.id else null
        val searchFilter = searchFilter.value
        val sort = sortBy.value?.apiValue ?: 1
        val index = pageNumber.value * PAGE_SIZE

        loadingStatus.value = AddModsLoadingStatus.LOADING

        runAsync {
            val addons =
                    curseApi.getCurseAddonSearch(432, minecraftVersion, 6, categoryId, searchFilter, sort, PAGE_SIZE,
                        index)
            runLater {
                loadedAddons.setAll(addons)
                loadingStatus.value = AddModsLoadingStatus.LOADED
            }
        }
    }

    fun displayModDetails(addon: AddonData) {
        find<ModDetailsFragment>(
            ModDetailsFragment::projectId to addon.id,
            ModDetailsFragment::changeVersionCallback to { newAddon: AddonId ->
                modListState.addAddon(newAddon)
            }
        ).openModal(modality = Modality.NONE, owner = find<AddModsView>().currentWindow)
    }

    fun displayModFiles(addon: AddonData) {
        find<ModVersionListFragment>(
            ModVersionListFragment::dialogType to ModVersionListFragment.Type.INSTALL,
            ModVersionListFragment::projectId to addon.id,
            ModVersionListFragment::selectCallback to { newAddon: AddonId ->
                modListState.addAddon(newAddon)
            }
        ).openModal(modality = Modality.NONE, owner = find<AddModsView>().currentWindow)
    }

    fun installLatest(addon: AddonData) {
        modListState.startEditing(addon.id)
        runAsync {
            val files = curseApi.getAddonFiles(addon.id).orEmpty()
            val latest = modListState.filterByMinecraftVersion(files).maxBy { it.fileDate }
            runLater {
                latest?.let {
                    modListState.addAddon(SimpleAddonId(addon.id, it.id))
                }
                modListState.finishEditing(addon.id)
            }
        }
    }
}

enum class AddModsLoadingStatus(val display: String) {
    NOTHING_LOADED("Nothing Loaded."),
    LOADING("Loading..."),
    LOADED("Done.");

    override fun toString(): String {
        return display
    }
}

enum class TwitchSearchSortBy(val display: String, val apiValue: Long) {
    POPULARITY("Popularity", 1),
    LAST_UPDATED("Last Updated", 2),
    NAME("Name", 3),
    AUTHOR("Author", 4),
    TOTAL_DOWNLOADS("Total Downloads", 5);

    override fun toString(): String {
        return display
    }
}
