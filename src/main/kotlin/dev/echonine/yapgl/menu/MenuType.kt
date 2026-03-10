package dev.echonine.yapgl.menu

/**
 * Represents an inventory window type, mapping to the container type IDs sent in WrapperPlayServerOpenWindow.
 *
 * @param id      The numeric container type ID recognized by the client.
 * @param size    The number of slots in the inventory
 */
enum class MenuType(val id: Int, val size: Int) {
    // Chest / generic inventories
    CHEST_1_ROW(0, 9),
    CHEST_2_ROWS(1, 18),
    CHEST_3_ROWS(2, 27),
    CHEST_4_ROWS(3, 36),
    CHEST_5_ROWS(4, 45),
    CHEST_6_ROWS(5, 54),

    // Standalone types
    DISPENSER(6, 9),
    DROPPER(6, 9),
    FURNACE(13, 3),
    BLAST_FURNACE(14, 3),
    SMOKER(15, 3),
    CRAFTING_TABLE(11, 10),
    ENCHANTING_TABLE(12, 2),
    BREWING_STAND(16, 5),
    BEACON(7, 1),
    ANVIL(8, 3),
    HOPPER(9, 5),
    SHULKER_BOX(10, 27),
    CARTOGRAPHY_TABLE(17, 3),
    GRINDSTONE(18, 3),
    LECTERN(19, 1),
    LOOM(20, 4),
    STONECUTTER(21, 2),
    SMITHING_TABLE(22, 4);

    companion object {
        fun chest(rows: Int): MenuType = when (rows) {
            1 -> CHEST_1_ROW
            2 -> CHEST_2_ROWS
            3 -> CHEST_3_ROWS
            4 -> CHEST_4_ROWS
            5 -> CHEST_5_ROWS
            6 -> CHEST_6_ROWS
            else -> throw IllegalArgumentException("Invalid row count: $rows. Must be 1–6.")
        }
    }
}