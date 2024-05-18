package com.verdantartifice.primalmagick.common.research;

import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.registries.RegistryKeysPM;
import com.verdantartifice.primalmagick.common.research.keys.ResearchEntryKey;
import com.verdantartifice.primalmagick.common.stats.StatsPM;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

/**
 * Datapack registry for the mod's research entries, the backbone of its progression system.
 * 
 * @author Daedalus4096
 */
public class ResearchEntries {
    // Fundamentals research entries
    public static final ResourceKey<ResearchEntry> FIRST_STEPS = create("first_steps");
    public static final ResourceKey<ResearchEntry> THEORYCRAFTING = create("theorycrafting");
    public static final ResourceKey<ResearchEntry> ATTUNEMENTS = create("attunements");
    public static final ResourceKey<ResearchEntry> LINGUISTICS = create("linguistics");
    public static final ResourceKey<ResearchEntry> UNLOCK_MANAWEAVING = create("unlock_manaweaving");
    public static final ResourceKey<ResearchEntry> UNLOCK_ALCHEMY = create("unlock_alchemy");
    public static final ResourceKey<ResearchEntry> UNLOCK_SORCERY = create("unlock_sorcery");
    public static final ResourceKey<ResearchEntry> UNLOCK_RUNEWORKING = create("unlock_runeworking");
    public static final ResourceKey<ResearchEntry> UNLOCK_RITUAL = create("unlock_ritual");
    public static final ResourceKey<ResearchEntry> UNLOCK_MAGITECH = create("unlock_magitech");
    public static final ResourceKey<ResearchEntry> UNLOCK_SCANS = create("unlock_scans");
    // TODO Place in correct order after bootstrapping
    public static final ResourceKey<ResearchEntry> UNLOCK_RUNE_ENCHANTMENTS = create("unlock_rune_enchantments");
    public static final ResourceKey<ResearchEntry> TERRESTRIAL_MAGICK = create("terrestrial_magick");
    public static final ResourceKey<ResearchEntry> SOURCE_EARTH = create("source_earth");
    public static final ResourceKey<ResearchEntry> SOURCE_SEA = create("source_sea");
    public static final ResourceKey<ResearchEntry> SOURCE_SKY = create("source_sky");
    public static final ResourceKey<ResearchEntry> SOURCE_SUN = create("source_sun");
    public static final ResourceKey<ResearchEntry> SOURCE_MOON = create("source_moon");
    public static final ResourceKey<ResearchEntry> FORBIDDEN_MAGICK = create("forbidden_magick");
    public static final ResourceKey<ResearchEntry> SOURCE_BLOOD = create("source_blood");
    public static final ResourceKey<ResearchEntry> SOURCE_INFERNAL = create("source_infernal");
    public static final ResourceKey<ResearchEntry> SOURCE_VOID = create("source_void");
    public static final ResourceKey<ResearchEntry> HEAVENLY_MAGICK = create("heavenly_magick");
    public static final ResourceKey<ResearchEntry> SOURCE_HALLOWED = create("source_hallowed");
    public static final ResourceKey<ResearchEntry> SECRETS_OF_THE_UNIVERSE = create("secrets_of_the_universe");
    public static final ResourceKey<ResearchEntry> COMPLETE_BASICS = create("complete_basics");
    public static final ResourceKey<ResearchEntry> THEORY_OF_EVERYTHING = create("theory_of_everything");
    
    // Manaweaving research entries
    // TODO Place in correct order after bootstrapping
    public static final ResourceKey<ResearchEntry> BASIC_MANAWEAVING = create("basic_manaweaving");
    public static final ResourceKey<ResearchEntry> EXPERT_MANAWEAVING = create("expert_manaweaving");
    public static final ResourceKey<ResearchEntry> MASTER_MANAWEAVING = create("master_manaweaving");
    public static final ResourceKey<ResearchEntry> SUPREME_MANAWEAVING = create("supreme_manaweaving");
    public static final ResourceKey<ResearchEntry> COMPLETE_MANAWEAVING = create("complete_manaweaving");
    public static final ResourceKey<ResearchEntry> MANA_ARROWS = create("mana_arrows");
    public static final ResourceKey<ResearchEntry> WAND_CHARGER = create("wand_charger");
    public static final ResourceKey<ResearchEntry> MANA_SALTS = create("mana_salts");
    public static final ResourceKey<ResearchEntry> ADVANCED_WANDMAKING = create("advanced_wandmaking");
    public static final ResourceKey<ResearchEntry> STAVES = create("staves");
    public static final ResourceKey<ResearchEntry> EARTHSHATTER_HAMMER = create("earthshatter_hammer");
    public static final ResourceKey<ResearchEntry> SUNLAMP = create("sunlamp");
    public static final ResourceKey<ResearchEntry> IMBUED_WOOL = create("imbued_wool");
    public static final ResourceKey<ResearchEntry> SPELLCLOTH = create("spellcloth");
    public static final ResourceKey<ResearchEntry> HEXWEAVE = create("hexweave");
    public static final ResourceKey<ResearchEntry> SAINTSWOOL = create("saintswool");
    public static final ResourceKey<ResearchEntry> ARTIFICIAL_MANA_FONTS = create("artificial_mana_fonts");
    public static final ResourceKey<ResearchEntry> FORBIDDEN_MANA_FONTS = create("forbidden_mana_fonts");
    public static final ResourceKey<ResearchEntry> HEAVENLY_MANA_FONTS = create("heavenly_mana_fonts");
    public static final ResourceKey<ResearchEntry> ESSENCE_CASK_ENCHANTED = create("essence_cask_enchanted");
    public static final ResourceKey<ResearchEntry> ESSENCE_CASK_FORBIDDEN = create("essence_cask_forbidden");
    public static final ResourceKey<ResearchEntry> ESSENCE_CASK_HEAVENLY = create("essence_cask_heavenly");
    public static final ResourceKey<ResearchEntry> WAND_GLAMOUR_TABLE = create("wand_glamour_table");
    public static final ResourceKey<ResearchEntry> ATTUNEMENT_SHACKLES = create("attunement_shackles");
    public static final ResourceKey<ResearchEntry> WAND_CORE_HEARTWOOD = create("wand_core_heartwood");
    public static final ResourceKey<ResearchEntry> WAND_CORE_OBSIDIAN = create("wand_core_obsidian");
    public static final ResourceKey<ResearchEntry> WAND_CORE_CORAL = create("wand_core_coral");
    public static final ResourceKey<ResearchEntry> WAND_CORE_BAMBOO = create("wand_core_bamboo");
    public static final ResourceKey<ResearchEntry> WAND_CORE_SUNWOOD = create("wand_core_sunwood");
    public static final ResourceKey<ResearchEntry> WAND_CORE_MOONWOOD = create("wand_core_moonwood");
    public static final ResourceKey<ResearchEntry> WAND_CORE_BONE = create("wand_core_bone");
    public static final ResourceKey<ResearchEntry> WAND_CORE_BLAZE_ROD = create("wand_core_blaze_rod");
    public static final ResourceKey<ResearchEntry> WAND_CORE_PURPUR = create("wand_core_purpur");
    public static final ResourceKey<ResearchEntry> WAND_CAP_IRON = create("wand_cap_iron");
    public static final ResourceKey<ResearchEntry> WAND_CAP_GOLD = create("wand_cap_gold");
    public static final ResourceKey<ResearchEntry> WAND_CAP_PRIMALITE = create("wand_cap_primalite");
    public static final ResourceKey<ResearchEntry> WAND_CAP_HEXIUM = create("wand_cap_hexium");
    public static final ResourceKey<ResearchEntry> WAND_CAP_HALLOWSTEEL = create("wand_cap_hallowsteel");
    public static final ResourceKey<ResearchEntry> WAND_GEM_APPRENTICE = create("wand_gem_apprentice");
    public static final ResourceKey<ResearchEntry> WAND_GEM_ADEPT = create("wand_gem_adept");
    public static final ResourceKey<ResearchEntry> WAND_GEM_WIZARD = create("wand_gem_wizard");
    public static final ResourceKey<ResearchEntry> WAND_GEM_ARCHMAGE = create("wand_gem_archmage");

    // Alchemy research entries
    // TODO Place in correct order after bootstrapping
    public static final ResourceKey<ResearchEntry> BASIC_ALCHEMY = create("basic_alchemy");
    public static final ResourceKey<ResearchEntry> EXPERT_ALCHEMY = create("expert_alchemy");
    public static final ResourceKey<ResearchEntry> MASTER_ALCHEMY = create("master_alchemy");
    public static final ResourceKey<ResearchEntry> SUPREME_ALCHEMY = create("supreme_alchemy");
    public static final ResourceKey<ResearchEntry> COMPLETE_ALCHEMY = create("complete_alchemy");
    public static final ResourceKey<ResearchEntry> STONEMELDING = create("stonemelding");
    public static final ResourceKey<ResearchEntry> SKYGLASS = create("skyglass");
    public static final ResourceKey<ResearchEntry> SHARD_SYNTHESIS = create("shard_synthesis");
    public static final ResourceKey<ResearchEntry> SHARD_DESYNTHESIS = create("shard_desynthesis");
    public static final ResourceKey<ResearchEntry> PRIMALITE = create("primalite");
    public static final ResourceKey<ResearchEntry> CRYSTAL_SYNTHESIS = create("crystal_synthesis");
    public static final ResourceKey<ResearchEntry> CRYSTAL_DESYNTHESIS = create("crystal_desynthesis");
    public static final ResourceKey<ResearchEntry> HEXIUM = create("hexium");
    public static final ResourceKey<ResearchEntry> CLUSTER_SYNTHESIS = create("cluster_synthesis");
    public static final ResourceKey<ResearchEntry> CLUSTER_DESYNTHESIS = create("cluster_desynthesis");
    public static final ResourceKey<ResearchEntry> HALLOWSTEEL = create("hallowsteel");
    public static final ResourceKey<ResearchEntry> CALCINATOR_BASIC = create("calcinator_basic");
    public static final ResourceKey<ResearchEntry> CALCINATOR_ENCHANTED = create("calcinator_enchanted");
    public static final ResourceKey<ResearchEntry> CALCINATOR_FORBIDDEN = create("calcinator_forbidden");
    public static final ResourceKey<ResearchEntry> CALCINATOR_HEAVENLY = create("calcinator_heavenly");
    public static final ResourceKey<ResearchEntry> CRYOTREATMENT = create("cryotreatment");
    public static final ResourceKey<ResearchEntry> SANGUINE_CRUCIBLE = create("sanguine_crucible");
    public static final ResourceKey<ResearchEntry> SANGUINE_CORE_LAND_ANIMALS = create("sanguine_core_land_animals");
    public static final ResourceKey<ResearchEntry> SANGUINE_CORE_SEA_CREATURES = create("sanguine_core_sea_creatures");
    public static final ResourceKey<ResearchEntry> SANGUINE_CORE_FLYING_CREATURES = create("sanguine_core_flying_creatures");
    public static final ResourceKey<ResearchEntry> SANGUINE_CORE_PLANTS = create("sanguine_core_plants");
    public static final ResourceKey<ResearchEntry> SANGUINE_CORE_UNDEAD = create("sanguine_core_undead");
    public static final ResourceKey<ResearchEntry> SANGUINE_CORE_SAPIENTS = create("sanguine_core_sapients");
    public static final ResourceKey<ResearchEntry> SANGUINE_CORE_DEMONS = create("sanguine_core_demons");
    public static final ResourceKey<ResearchEntry> SANGUINE_CORE_ALIENS = create("sanguine_core_aliens");
    public static final ResourceKey<ResearchEntry> IGNYX = create("ignyx");
    public static final ResourceKey<ResearchEntry> SYNTHETIC_GEM_BUDS = create("synthetic_gem_buds");

    // Sorcery research entries
    // TODO Place in correct order after bootstrapping
    public static final ResourceKey<ResearchEntry> BASIC_SORCERY = create("basic_sorcery");
    public static final ResourceKey<ResearchEntry> EXPERT_SORCERY = create("expert_sorcery");
    public static final ResourceKey<ResearchEntry> MASTER_SORCERY = create("master_sorcery");
    public static final ResourceKey<ResearchEntry> SUPREME_SORCERY = create("supreme_sorcery");
    public static final ResourceKey<ResearchEntry> COMPLETE_SORCERY = create("complete_sorcery");
    public static final ResourceKey<ResearchEntry> WAND_INSCRIPTION = create("wand_inscription");
    public static final ResourceKey<ResearchEntry> SPELL_VEHICLE_PROJECTILE = create("spell_vehicle_projectile");
    public static final ResourceKey<ResearchEntry> SPELL_VEHICLE_BOLT = create("spell_vehicle_bolt");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_FROST = create("spell_payload_frost");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_LIGHTNING = create("spell_payload_lightning");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_SOLAR = create("spell_payload_solar");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_LUNAR = create("spell_payload_lunar");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_BLOOD = create("spell_payload_blood");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_FLAME = create("spell_payload_flame");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_VOID = create("spell_payload_void");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_HOLY = create("spell_payload_holy");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_BREAK = create("spell_payload_break");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_CONJURE_STONE = create("spell_payload_conjure_stone");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_CONJURE_WATER = create("spell_payload_conjure_water");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_SHEAR = create("spell_payload_shear");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_FLIGHT = create("spell_payload_flight");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_CONJURE_LIGHT = create("spell_payload_conjure_light");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_HEALING = create("spell_payload_healing");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_POLYMORPH = create("spell_payload_polymorph");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_POLYMORPH_SHEEP = create("spell_payload_polymorph_sheep");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_CONJURE_ANIMAL = create("spell_payload_conjure_animal");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_CONJURE_LAVA = create("spell_payload_conjure_lava");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_DRAIN_SOUL = create("spell_payload_drain_soul");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_TELEPORT = create("spell_payload_teleport");
    public static final ResourceKey<ResearchEntry> SPELL_PAYLOAD_CONSECRATE = create("spell_payload_consecrate");
    public static final ResourceKey<ResearchEntry> SPELL_MOD_AMPLIFY = create("spell_mod_amplify");
    public static final ResourceKey<ResearchEntry> SPELL_MOD_BURST = create("spell_mod_burst");
    public static final ResourceKey<ResearchEntry> SPELL_MOD_QUICKEN = create("spell_mod_quicken");
    public static final ResourceKey<ResearchEntry> SPELL_MOD_MINE = create("spell_mod_mine");
    public static final ResourceKey<ResearchEntry> SPELL_MOD_FORK = create("spell_mod_fork");
    
    // Runeworking research entries
    // TODO Place in correct order after bootstrapping
    public static final ResourceKey<ResearchEntry> BASIC_RUNEWORKING = create("basic_runeworking");
    public static final ResourceKey<ResearchEntry> EXPERT_RUNEWORKING = create("expert_runeworking");
    public static final ResourceKey<ResearchEntry> MASTER_RUNEWORKING = create("master_runeworking");
    public static final ResourceKey<ResearchEntry> SUPREME_RUNEWORKING = create("supreme_runeworking");
    public static final ResourceKey<ResearchEntry> COMPLETE_RUNEWORKING = create("complete_runeworking");
    public static final ResourceKey<ResearchEntry> RUNE_EARTH = create("rune_earth");
    public static final ResourceKey<ResearchEntry> RUNE_SEA = create("rune_sea");
    public static final ResourceKey<ResearchEntry> RUNE_SKY = create("rune_sky");
    public static final ResourceKey<ResearchEntry> RUNE_SUN = create("rune_sun");
    public static final ResourceKey<ResearchEntry> RUNE_MOON = create("rune_moon");
    public static final ResourceKey<ResearchEntry> RUNE_BLOOD = create("rune_blood");
    public static final ResourceKey<ResearchEntry> RUNE_INFERNAL = create("rune_infernal");
    public static final ResourceKey<ResearchEntry> RUNE_VOID = create("rune_void");
    public static final ResourceKey<ResearchEntry> RUNE_HALLOWED = create("rune_hallowed");
    public static final ResourceKey<ResearchEntry> RUNE_PROJECT = create("rune_project");
    public static final ResourceKey<ResearchEntry> RUNE_PROTECT = create("rune_protect");
    public static final ResourceKey<ResearchEntry> RUNE_ABSORB = create("rune_absorb");
    public static final ResourceKey<ResearchEntry> RUNE_DISPEL = create("rune_dispel");
    public static final ResourceKey<ResearchEntry> RUNE_SUMMON = create("rune_summon");
    public static final ResourceKey<ResearchEntry> RUNE_ITEM = create("rune_item");
    public static final ResourceKey<ResearchEntry> RUNE_SELF = create("rune_self");
    public static final ResourceKey<ResearchEntry> RUNE_CREATURE = create("rune_creature");
    public static final ResourceKey<ResearchEntry> RUNE_AREA = create("rune_area");
    public static final ResourceKey<ResearchEntry> RUNE_INSIGHT = create("rune_insight");
    public static final ResourceKey<ResearchEntry> RUNE_POWER = create("rune_power");
    public static final ResourceKey<ResearchEntry> RUNE_GRACE = create("rune_grace");
    public static final ResourceKey<ResearchEntry> RUNIC_GRINDSTONE = create("runic_grindstone");
    public static final ResourceKey<ResearchEntry> RECALL_STONE = create("recall_stone");
    public static final ResourceKey<ResearchEntry> RUNIC_TRIM = create("runic_trim");
    public static final ResourceKey<ResearchEntry> ENDERWARD = create("enderward");
    
    // Ritual Magick research entries
    // TODO Place in correct order after bootstrapping
    public static final ResourceKey<ResearchEntry> BASIC_RITUAL = create("basic_ritual");
    public static final ResourceKey<ResearchEntry> EXPERT_RITUAL = create("expert_ritual");
    public static final ResourceKey<ResearchEntry> MASTER_RITUAL = create("master_ritual");
    public static final ResourceKey<ResearchEntry> SUPREME_RITUAL = create("supreme_ritual");
    public static final ResourceKey<ResearchEntry> COMPLETE_RITUAL = create("complete_ritual");
    public static final ResourceKey<ResearchEntry> MANAFRUIT = create("manafruit");
    public static final ResourceKey<ResearchEntry> AMBROSIA = create("ambrosia");

    // TODO Magitech research entries
    // TODO Place in correct order after bootstrapping
    
    // Scans research entries
    // TODO Place in correct order after bootstrapping
    public static final ResourceKey<ResearchEntry> RAW_MARBLE = create("raw_marble");
    public static final ResourceKey<ResearchEntry> ROCK_SALT = create("rock_salt");
    public static final ResourceKey<ResearchEntry> QUARTZ = create("quartz");
    public static final ResourceKey<ResearchEntry> SUNWOOD_TREES = create("sunwood_trees");
    public static final ResourceKey<ResearchEntry> MOONWOOD_TREES = create("moonwood_trees");
    public static final ResourceKey<ResearchEntry> HALLOWED_ORB = create("hallowed_orb");
    public static final ResourceKey<ResearchEntry> HALLOWOOD_TREES = create("hallowood_trees");
    public static final ResourceKey<ResearchEntry> BOOKSHELF = create("bookshelf");
    public static final ResourceKey<ResearchEntry> BEEHIVE = create("beehive");
    public static final ResourceKey<ResearchEntry> BEACON = create("beacon");
    public static final ResourceKey<ResearchEntry> DRAGON_EGG = create("dragon_egg");
    public static final ResourceKey<ResearchEntry> DRAGON_HEAD = create("dragon_head");
    public static final ResourceKey<ResearchEntry> INNER_DEMON = create("inner_demon");
    public static final ResourceKey<ResearchEntry> TREEFOLK = create("treefolk");
    public static final ResourceKey<ResearchEntry> ALCHEMICAL_WASTE = create("alchemical_waste");
    public static final ResourceKey<ResearchEntry> MYSTICAL_RELIC = create("mystical_relic");
    public static final ResourceKey<ResearchEntry> HUMMING_ARTIFACT = create("humming_artifact");
    
    // Internal research entries
    public static final ResourceKey<ResearchEntry> DISCOVER_BLOOD = create("discover_blood");
    public static final ResourceKey<ResearchEntry> DISCOVER_INFERNAL = create("discover_infernal");
    public static final ResourceKey<ResearchEntry> DISCOVER_VOID = create("discover_void");
    public static final ResourceKey<ResearchEntry> DISCOVER_FORBIDDEN = create("discover_forbidden");
    public static final ResourceKey<ResearchEntry> DISCOVER_HALLOWED = create("discover_hallowed");
    // TODO Place in correct order after bootstrapping
    public static final ResourceKey<ResearchEntry> WAND_TRANSFORM_HINT = create("wand_transform_hint");
    public static final ResourceKey<ResearchEntry> FOUND_SHRINE = create("found_shrine");
    public static final ResourceKey<ResearchEntry> GOT_DREAM = create("got_dream");
    public static final ResourceKey<ResearchEntry> SIPHON_PROMPT = create("siphon_prompt");
    public static final ResourceKey<ResearchEntry> ENV_EARTH = create("env_earth");
    public static final ResourceKey<ResearchEntry> ENV_SEA = create("env_sea");
    public static final ResourceKey<ResearchEntry> ENV_SKY = create("env_sky");
    public static final ResourceKey<ResearchEntry> ENV_SUN = create("env_sun");
    public static final ResourceKey<ResearchEntry> ENV_MOON = create("env_moon");
    public static final ResourceKey<ResearchEntry> DROWN_A_LITTLE = create("drown_a_little");
    public static final ResourceKey<ResearchEntry> FEEL_THE_BURN = create("feel_the_burn");
    public static final ResourceKey<ResearchEntry> FURRY_FRIEND = create("furry_friend");
    public static final ResourceKey<ResearchEntry> BREED_ANIMAL = create("breed_animal");
    public static final ResourceKey<ResearchEntry> NEAR_DEATH_EXPERIENCE = create("near_death_experience");
    public static final ResourceKey<ResearchEntry> SOTU_DISCOVER_BLOOD = create("sotu_discover_blood");
    public static final ResourceKey<ResearchEntry> SOTU_DISCOVER_INFERNAL = create("sotu_discover_infernal");
    public static final ResourceKey<ResearchEntry> SOTU_DISCOVER_VOID = create("sotu_discover_void");
    public static final ResourceKey<ResearchEntry> SOTU_SCAN_HALLOWED_ORB = create("sotu_scan_hallowed_orb");
    public static final ResourceKey<ResearchEntry> SCAN_PRIMALITE = create("scan_primalite");
    public static final ResourceKey<ResearchEntry> SCAN_HEXIUM = create("scan_hexium");
    public static final ResourceKey<ResearchEntry> SCAN_HALLOWSTEEL = create("scan_hallowsteel");
    public static final ResourceKey<ResearchEntry> SCAN_FLYING_CREATURE = create("scan_flying_creature");
    public static final ResourceKey<ResearchEntry> SCAN_GOLEM = create("scan_golem");
    public static final ResourceKey<ResearchEntry> SCAN_NETHER_STAR = create("scan_nether_star");
    public static final ResourceKey<ResearchEntry> UNKNOWN_RUNE = create("unknown_rune");
    
    public static ResourceKey<ResearchEntry> create(String name) {
        return ResourceKey.create(RegistryKeysPM.RESEARCH_ENTRIES, PrimalMagick.resource(name));
    }
    
    public static void bootstrap(BootstapContext<ResearchEntry> context) {
        bootstrapBasicsEntries(context);
        bootstrapInternalEntries(context);
    }
    
    private static void bootstrapBasicsEntries(BootstapContext<ResearchEntry> context) {
        ResourceKey<ResearchDiscipline> discipline = ResearchDisciplines.BASICS;
        register(context, FIRST_STEPS, key -> ResearchEntry.builder(key).discipline(discipline).icon(ItemsPM.GRIMOIRE.get())
                .stage().requiredCraft(ItemsPM.ARCANE_WORKBENCH.get()).recipe(ItemsPM.MUNDANE_WAND.get()).end()
                .stage().requiredStat(StatsPM.MANA_SIPHONED, 10).recipe(ItemsPM.MUNDANE_WAND.get()).end()
                .stage().requiredStat(StatsPM.OBSERVATIONS_MADE, 1).recipe(ItemsPM.MUNDANE_WAND.get()).recipe(ItemsPM.WOOD_TABLE.get()).recipe(ItemsPM.MAGNIFYING_GLASS.get())
                        .recipe(ItemsPM.ANALYSIS_TABLE.get()).end()
                .stage().recipe(ItemsPM.MUNDANE_WAND.get()).recipe(ItemsPM.WOOD_TABLE.get()).recipe(ItemsPM.MAGNIFYING_GLASS.get()).recipe(ItemsPM.ANALYSIS_TABLE.get()).end()
                .build());
        register(context, THEORYCRAFTING, key -> ResearchEntry.builder(key).discipline(discipline).icon("textures/research/knowledge_theory.png").parent(FIRST_STEPS)
                .stage().requiredKnowledge(KnowledgeType.OBSERVATION, 1).end()
                .stage().requiredCraft(ItemsPM.RESEARCH_TABLE.get()).requiredCraft(ItemsPM.ENCHANTED_INK_AND_QUILL.get()).recipe(ItemsPM.RESEARCH_TABLE.get()).recipe(ItemsPM.ENCHANTED_INK.get())
                        .recipe(ItemsPM.ENCHANTED_INK_AND_QUILL.get()).end()
                .stage().requiredStat(StatsPM.THEORIES_FORMED, 1).recipe(ItemsPM.RESEARCH_TABLE.get()).recipe(ItemsPM.ENCHANTED_INK.get()).recipe(ItemsPM.ENCHANTED_INK_AND_QUILL.get()).end()
                .stage().recipe(ItemsPM.RESEARCH_TABLE.get()).recipe(ItemsPM.ENCHANTED_INK.get()).recipe(ItemsPM.ENCHANTED_INK_AND_QUILL.get()).end()
                .build());
        register(context, ATTUNEMENTS, key -> ResearchEntry.builder(key).discipline(discipline).parent(FIRST_STEPS)
                .stage().requiredKnowledge(KnowledgeType.OBSERVATION, 1).end()
                .stage().end()
                .build());
        // FIXME Re-add for 1.21 release
/*
        register(context, LINGUISTICS, key -> ResearchEntry.builder(key).discipline(discipline).icon(Items.WRITABLE_BOOK).parent(FIRST_STEPS)
                .stage().requiredKnowledge(KnowledgeType.OBSERVATION, 1).requiredStat(StatsPM.ANCIENT_BOOKS_READ, 1).end()
                .stage().recipe(ItemsPM.SCRIBE_TABLE.get()).end()
                .build());
*/
        register(context, UNLOCK_MANAWEAVING, key -> ResearchEntry.builder(key).discipline(discipline).icon("textures/research/discipline_manaweaving.png").parent(FIRST_STEPS)
                .stage().requiredKnowledge(KnowledgeType.OBSERVATION, 1).end()
                .stage().end()
                .build());
        register(context, UNLOCK_ALCHEMY, key -> ResearchEntry.builder(key).discipline(discipline).icon("textures/research/discipline_alchemy.png").parent(MANA_ARROWS)
                .stage().requiredKnowledge(KnowledgeType.OBSERVATION, 1).end()
                .stage().end()
                .build());
        register(context, UNLOCK_SORCERY, key -> ResearchEntry.builder(key).discipline(discipline).icon("textures/research/discipline_sorcery.png").parent(WAND_CHARGER)
                .stage().requiredKnowledge(KnowledgeType.OBSERVATION, 1).end()
                .stage().end()
                .build());
        register(context, UNLOCK_RUNEWORKING, key -> ResearchEntry.builder(key).discipline(discipline).icon("textures/research/discipline_runeworking.png").parent(CALCINATOR_BASIC)
                .stage().requiredKnowledge(KnowledgeType.OBSERVATION, 1).end()
                .stage().end()
                .build());
        register(context, UNLOCK_RITUAL, key -> ResearchEntry.builder(key).discipline(discipline).icon("textures/research/discipline_ritual.png").parent(WAND_INSCRIPTION).parent(RUNE_PROJECT)
                .stage().requiredKnowledge(KnowledgeType.OBSERVATION, 1).end()
                .stage().end()
                .build());
        register(context, UNLOCK_MAGITECH, key -> ResearchEntry.builder(key).discipline(discipline).icon("textures/research/discipline_magitech.png").parent(MANAFRUIT).parent(MANA_SALTS)
                .stage().requiredKnowledge(KnowledgeType.OBSERVATION, 1).end()
                .stage().end()
                .build());
    }
    
    private static void bootstrapInternalEntries(BootstapContext<ResearchEntry> context) {
        register(context, DISCOVER_BLOOD, key -> ResearchEntry.builder(key).internal().icon("textures/research/research_tube.png")
                .stage().sibling(DISCOVER_FORBIDDEN).sibling(SOTU_DISCOVER_BLOOD).end()
                .build());
        register(context, DISCOVER_INFERNAL, key -> ResearchEntry.builder(key).internal().icon("textures/research/research_tube.png")
                .stage().sibling(DISCOVER_FORBIDDEN).sibling(SOTU_DISCOVER_INFERNAL).end()
                .build());
        register(context, DISCOVER_VOID, key -> ResearchEntry.builder(key).internal().icon("textures/research/research_tube.png")
                .stage().sibling(DISCOVER_FORBIDDEN).sibling(SOTU_DISCOVER_VOID).end()
                .build());
        register(context, DISCOVER_FORBIDDEN, key -> ResearchEntry.builder(key).internal().build());
        register(context, DISCOVER_HALLOWED, key -> ResearchEntry.builder(key).internal().icon("textures/research/research_tube.png").build());
    }
    
    private static Holder.Reference<ResearchEntry> register(BootstapContext<ResearchEntry> context, ResourceKey<ResearchEntry> key, Function<ResourceKey<ResearchEntry>, ResearchEntry> supplier) {
        return context.register(key, supplier.apply(key));
    }
    
    @Nullable
    public static ResearchEntry getEntry(RegistryAccess registryAccess, ResearchEntryKey key) {
        return getEntry(registryAccess, key.getRootKey());
    }
    
    @Nullable
    public static ResearchEntry getEntry(RegistryAccess registryAccess, ResourceKey<ResearchEntry> rawKey) {
        return registryAccess.registryOrThrow(RegistryKeysPM.RESEARCH_ENTRIES).get(rawKey);
    }
    
    public static Stream<ResearchEntry> stream(RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(RegistryKeysPM.RESEARCH_ENTRIES).stream();
    }
}
