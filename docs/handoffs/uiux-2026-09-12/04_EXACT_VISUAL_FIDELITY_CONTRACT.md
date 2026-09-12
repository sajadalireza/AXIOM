# EXACT VISUAL FIDELITY CONTRACT

Status: Product Owner binding requirement for WP-UIUX-01 and all later UI/UX slices that reuse this handoff.

## 1. Intent
The selected mockups are not generic inspiration. For visual design, they are the target composition to reproduce as closely as practical in native Android Compose while preserving canonical product behavior, accessibility, localization, and device adaptability.

Repository/governance remains authoritative for product capabilities, logic, data, routes, privacy, and module status. The selected visual reference is authoritative for the appearance of the authorized screen when it does not conflict with those sources.

## 2. Binding visual dimensions
For each authorized screen with an approved target reference, implementation MUST preserve the reference's:
- information hierarchy and content grouping
- relative vertical rhythm and whitespace
- hero/artwork position, scale, crop, and visual weight
- card count, card arrangement, geometry, corner treatment, border treatment, and elevation/glow character
- CTA count, CTA location, width hierarchy, and visual prominence
- icon placement and semantic role
- typography hierarchy, alignment, approximate scale relationships, line breaks where practical, and emphasis
- Deep Emerald / restrained Champagne Gold / Graphite color relationships
- navigation treatment when the authorized slice includes navigation
- overall density and calm premium character

Do not substitute a different design merely because it is easier to implement.
Do not flatten the selected cinematic composition into a generic Material screen.
Do not add terminal/RPG decoration that is absent from the selected target.

## 3. Allowed deviations
Deviation is permitted only when required by:
1. canonical behavior or data model,
2. Android system insets / safe areas,
3. responsive device width/height,
4. EN/FA localization and RTL mirroring,
5. 200% font scaling,
6. TalkBack or >=48dp target requirements,
7. reduced motion,
8. platform rendering constraints where an equivalent native treatment is used.

Every material deviation MUST be listed in the evidence artifact with reason and impact. Aesthetic preference by the implementer is not a valid reason.

## 4. Missing-reference rule
If an authorized screen does not have an exact approved target reference in the handoff, STOP before visually redesigning that screen. Do not invent a replacement. Ask for Product Owner reference selection.

## 5. Screen-to-reference matrix
Before editing, produce a matrix:
`Screen / Approved reference / Canonical behavior source / Required content adaptations / Expected deviations`.

Only references explicitly marked approved in `03_VISUAL_REFERENCE_MANIFEST.md` may be used as visual targets. Runtime/baseline images are comparison evidence, not targets.

## 6. Visual acceptance loop
For every screen:
1. Render the approved target reference side-by-side with a fresh emulator screenshot at a comparable viewport.
2. Review at minimum: hierarchy, spacing, artwork crop/weight, card geometry, CTA placement, typography hierarchy, iconography, colors, and overall density.
3. Record visual deltas as Critical / Major / Minor.
4. Repair every Critical and Major delta that is not justified by Section 3.
5. Re-capture and repeat.

A build/test PASS is necessary but NOT sufficient for Product Owner visual acceptance.

## 7. Acceptance MUST
WP-UIUX-01 cannot be marked design-accepted unless:
- all canonical functional/accessibility requirements pass,
- every authorized screen with a target reference completed the visual comparison loop,
- zero unjustified Critical visual deltas remain,
- zero unjustified Major visual deltas remain,
- all intentional deviations are documented,
- final screenshots are preserved next to their target references for review.

The previous numerical score does not override this Product Owner visual-fidelity requirement.

## 8. Future slices
The same rule applies to Home, Missions, Physical, Shadows, Hunter, and Xion when their packet is explicitly authorized. Never infer an unprovided target from another module or generated board.
