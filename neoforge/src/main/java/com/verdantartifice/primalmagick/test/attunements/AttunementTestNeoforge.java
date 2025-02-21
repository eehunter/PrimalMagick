package com.verdantartifice.primalmagick.test.attunements;

import com.verdantartifice.primalmagick.Constants;
import com.verdantartifice.primalmagick.test.TestUtilsNeoforge;
import net.minecraft.gametest.framework.BeforeBatch;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.Collection;

@GameTestHolder(Constants.MOD_ID)
public class AttunementTestNeoforge extends AbstractAttunementTest {
    @BeforeBatch(batch = "attunementDayTests")
    @Override
    public void beforeDayBatch(ServerLevel level) {
        super.beforeDayBatch(level);
    }

    @BeforeBatch(batch = "attunementNightTests")
    @Override
    public void beforeNightBatch(ServerLevel level) {
        super.beforeNightBatch(level);
    }

    @GameTestGenerator
    public Collection<TestFunction> minor_attunement_gives_mana_discount() {
        return super.minor_attunement_gives_mana_discount(TestUtilsNeoforge.DEFAULT_GENERATOR_TEMPLATE);
    }

    @PrefixGameTestTemplate(false)
    @GameTest(template = TestUtilsNeoforge.DEFAULT_TEMPLATE)
    @Override
    public void lesser_earth_attunement_gives_haste_modifier(GameTestHelper helper) {
        super.lesser_earth_attunement_gives_haste_modifier(helper);
    }

    @PrefixGameTestTemplate(false)
    @GameTest(template = TestUtilsNeoforge.DEFAULT_TEMPLATE)
    @Override
    public void greater_earth_attunement_gives_step_height_modifier(GameTestHelper helper) {
        super.greater_earth_attunement_gives_step_height_modifier(helper);
    }

    @PrefixGameTestTemplate(false)
    @GameTest(template = TestUtilsNeoforge.DEFAULT_TEMPLATE)
    @Override
    public void lesser_sea_attunement_gives_increased_swim_speed(GameTestHelper helper) {
        super.lesser_sea_attunement_gives_increased_swim_speed(helper);
    }

    @PrefixGameTestTemplate(false)
    @GameTest(template = TestUtilsNeoforge.DEFAULT_TEMPLATE)
    @Override
    public void greater_sea_attunement_gives_water_breathing(GameTestHelper helper) {
        super.greater_sea_attunement_gives_water_breathing(helper);
    }

    @PrefixGameTestTemplate(false)
    @GameTest(template = TestUtilsNeoforge.DEFAULT_TEMPLATE)
    @Override
    public void lesser_sky_attunement_gives_movement_speed_modifier(GameTestHelper helper) {
        super.lesser_sky_attunement_gives_movement_speed_modifier(helper);
    }

    @PrefixGameTestTemplate(false)
    @GameTest(template = TestUtilsNeoforge.DEFAULT_TEMPLATE)
    @Override
    public void lesser_sky_attunement_reduces_fall_damage_taken(GameTestHelper helper) {
        super.lesser_sky_attunement_reduces_fall_damage_taken(helper);
    }

    @PrefixGameTestTemplate(false)
    @GameTest(template = TestUtilsNeoforge.DEFAULT_TEMPLATE)
    @Override
    public void greater_sky_attunement_increases_jump_strength(GameTestHelper helper) {
        super.greater_sky_attunement_increases_jump_strength(helper);
    }

    @PrefixGameTestTemplate(false)
    @GameTest(template = TestUtilsNeoforge.DEFAULT_TEMPLATE, batch = "attunementDayTests")
    @Override
    public void lesser_sun_attunement_regenerates_food_during_day(GameTestHelper helper) {
        super.lesser_sun_attunement_regenerates_food_during_day(helper);
    }

    @PrefixGameTestTemplate(false)
    @GameTest(template = TestUtilsNeoforge.DEFAULT_TEMPLATE, batch = "attunementNightTests")
    @Override
    public void lesser_sun_attunement_does_not_regenerate_food_during_night(GameTestHelper helper) {
        super.lesser_sun_attunement_does_not_regenerate_food_during_night(helper);
    }

    @PrefixGameTestTemplate(false)
    @GameTest(template = TestUtilsNeoforge.DEFAULT_TEMPLATE)
    @Override
    public void lesser_moon_attunement_grants_invisibility_chance_on_hurt(GameTestHelper helper) {
        super.lesser_moon_attunement_grants_invisibility_chance_on_hurt(helper);
    }

    @PrefixGameTestTemplate(false)
    @GameTest(template = TestUtilsNeoforge.DEFAULT_TEMPLATE)
    @Override
    public void greater_moon_attunement_grants_night_vision(GameTestHelper helper) {
        super.greater_moon_attunement_grants_night_vision(helper);
    }
}
