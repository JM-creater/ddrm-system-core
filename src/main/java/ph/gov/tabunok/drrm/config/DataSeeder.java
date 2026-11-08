package ph.gov.tabunok.drrm.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ph.gov.tabunok.drrm.functions.guide.entity.Guide;
import ph.gov.tabunok.drrm.functions.directory.entity.Hotline;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentType;
import ph.gov.tabunok.drrm.functions.quiz.entity.QuizQuestion;
import ph.gov.tabunok.drrm.functions.user.entity.Role;
import ph.gov.tabunok.drrm.functions.survey.entity.SurveyQuestion;
import ph.gov.tabunok.drrm.functions.survey.entity.SurveySection;
import ph.gov.tabunok.drrm.functions.user.entity.User;
import ph.gov.tabunok.drrm.functions.guide.repository.GuideRepository;
import ph.gov.tabunok.drrm.functions.directory.repository.HotlineRepository;
import ph.gov.tabunok.drrm.functions.quiz.repository.QuizQuestionRepository;
import ph.gov.tabunok.drrm.functions.survey.repository.SurveyQuestionRepository;
import ph.gov.tabunok.drrm.functions.user.repository.UserRepository;

/**
 * Loads reference data on first start-up: the officer account, the KAP research instrument,
 * the knowledge test, general preparedness guides and national hotlines.
 * Every block only runs when its table is still empty, so restarting the server is safe.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SurveyQuestionRepository surveyQuestionRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final GuideRepository guideRepository;
    private final HotlineRepository hotlineRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties properties;

    @Override
    @Transactional
    public void run(String... args) {
        seedAdmin();
        seedSurvey();
        seedQuiz();
        seedGuides();
        seedHotlines();
    }

    private void seedAdmin() {
        String email = properties.getSeed().getAdminEmail();
        String password = properties.getSeed().getAdminPassword();
        if (email == null || email.isBlank() || password == null || password.isBlank()) return;
        if (userRepository.existsByEmailIgnoreCase(email)) return;
        userRepository.save(User.builder()
                .firstName("Barangay")
                .lastName("DRRM Officer")
                .email(email.trim().toLowerCase())
                .password(passwordEncoder.encode(password))
                .role(Role.ADMIN)
                .enabled(true)
                .build());
        log.info("Created officer account {}. Change the password after the first sign-in.", email);
    }

    private void seedSurvey() {
        if (surveyQuestionRepository.count() > 0) return;
        List<SurveyQuestion> items = new ArrayList<>();

        SurveySection k = SurveySection.KNOWLEDGE;
        items.add(q(k, 1, false, "Hazard awareness",
                "I am aware of the common disasters (e.g., typhoons, floods) that affect our area."));
        items.add(q(k, 2, true, "Evacuation centers",
                "I do not know where the designated evacuation centers in our barangay are located."));
        items.add(q(k, 3, false, "Warning signals",
                "I clearly understand the meaning of warning signals issued by authorities (e.g., storm signals, flood alerts)."));
        items.add(q(k, 4, true, "Emergency hotlines",
                "I am unfamiliar with emergency hotline numbers to contact during disasters."));
        items.add(q(k, 5, false, "Go-bag",
                "I am fully aware of the importance of preparing an emergency go-bag."));
        items.add(q(k, 6, true, "Emergency kit",
                "I do not know which essential items should be placed in an emergency kit."));
        items.add(q(k, 7, false, "Risk awareness",
                "I understand the risks of living in flood-prone or coastal areas."));
        items.add(q(k, 8, true, "Community programs",
                "I have not heard about any disaster preparedness programs in our community."));
        items.add(q(k, 9, false, "Response actions",
                "I know exactly what actions to take before, during, and after a disaster."));
        items.add(q(k, 10, true, "Evacuation procedures",
                "I do not know the evacuation procedures implemented by local authorities."));

        SurveySection a = SurveySection.ATTITUDE;
        items.add(q(a, 1, false, "Attention to warnings",
                "I pay close attention to disaster warnings, advisories, and alerts issued by authorities."));
        items.add(q(a, 2, true, "Personal responsibility",
                "I feel that preparing my household for disasters is not my personal responsibility."));
        items.add(q(a, 3, false, "Willingness to learn",
                "I am willing to learn how to better respond to disaster alerts and attend related training."));
        items.add(q(a, 4, true, "Timely action",
                "I usually ignore or delay acting on disaster warnings until the situation worsens."));
        items.add(q(a, 5, false, "Value of quick response",
                "I believe that responding quickly to alerts can greatly reduce disaster harm."));
        items.add(q(a, 6, true, "Drill participation",
                "I think participating in community alert drills is unnecessary and a waste of time."));
        items.add(q(a, 7, false, "Confidence",
                "I feel confident that I know what to do when I receive an official disaster alert."));
        items.add(q(a, 8, true, "Trust in LGU alerts",
                "I believe that local government disaster alerts are generally unreliable or unhelpful."));
        items.add(q(a, 9, false, "Risk concern",
                "I stay alert and updated because I am concerned about disaster risks in our area."));
        items.add(q(a, 10, true, "Household readiness",
                "I feel that there is no need for households to remain ready whenever alerts are raised."));

        SurveySection p = SurveySection.PRACTICE;
        items.add(q(p, 1, false, "Go-bag",
                "I have prepared an emergency go-bag and kept it ready at home."));
        items.add(q(p, 2, true, "Monitoring advisories",
                "I rarely check weather updates or disaster advisories from authorities."));
        items.add(q(p, 3, false, "Evacuation routes",
                "I have identified and know the evacuation routes in my community."));
        items.add(q(p, 4, true, "Drill participation",
                "I do not participate in disaster or alert drills conducted in my barangay."));
        items.add(q(p, 5, false, "Family plan",
                "I have discussed emergency plans and alert responses with my family members."));
        items.add(q(p, 6, true, "Stored supplies",
                "I do not store enough supplies such as food, water, and medicine for emergencies."));
        items.add(q(p, 7, false, "Following authorities",
                "I follow instructions from authorities during disasters."));
        items.add(q(p, 8, false, "Important documents",
                "I secure important documents in case of emergencies."));
        items.add(q(p, 9, false, "Home mitigation",
                "I regularly prepare my home to minimize disaster damage (e.g., securing structures, roofing, drainage)."));
        items.add(q(p, 10, true, "Helping neighbors",
                "I do not inform or assist my neighbors whenever disaster alerts are announced."));

        surveyQuestionRepository.saveAll(items);
        log.info("Loaded {} KAP questionnaire items.", items.size());
    }

    private static SurveyQuestion q(SurveySection section, int no, boolean negative, String indicator, String text) {
        return SurveyQuestion.builder()
                .section(section)
                .itemNo(no)
                .negative(negative)
                .indicator(indicator)
                .statement(text)
                .build();
    }

    private void seedQuiz() {
        if (quizQuestionRepository.count() > 0) return;
        List<QuizQuestion> items = List.of(
                quiz(1, "What does DRRM stand for?",
                        "Disaster Risk Reduction and Management",
                        "Disaster Relief and Rescue Mission",
                        "Department of Rescue and Recovery Management",
                        "Disaster Response and Rehabilitation Measures",
                        "A", "RA 10121 is the Philippine Disaster Risk Reduction and Management Act of 2010."),
                quiz(2, "You are indoors when strong ground shaking starts. What should you do first?",
                        "Run outside right away",
                        "Stand beside a glass window",
                        "Duck, cover, and hold under a sturdy table",
                        "Take the elevator down",
                        "C", "Most injuries happen while people try to move during shaking. Stay put, protect your head and neck, and wait until the shaking stops."),
                quiz(3, "PAGASA raises Tropical Cyclone Wind Signal No. 3. What does it mean?",
                        "Light rains only, no action needed",
                        "Storm-force winds expected within 18 hours; prepare to evacuate if advised",
                        "The typhoon has already left the country",
                        "Classes resume normally",
                        "B", "Signal No. 3 means storm-force winds (89 to 117 km/h) are expected. Secure the house and follow evacuation orders."),
                quiz(4, "Which item does NOT belong in an emergency go-bag?",
                        "Drinking water and ready-to-eat food",
                        "Flashlight with extra batteries",
                        "Copies of IDs and important documents in a plastic pouch",
                        "A full-size television",
                        "D", "A go-bag should be light enough to carry. Pack water, food, medicine, a flashlight, a whistle, documents, and a change of clothes for 72 hours."),
                quiz(5, "Floodwater has entered your street. Which is the safest action?",
                        "Wade or drive through the water to reach higher ground faster",
                        "Turn off the main power switch and move to higher ground",
                        "Let children play in the water while it is still shallow",
                        "Stay on the ground floor to guard your belongings",
                        "B", "Floodwater can hide open manholes, strong currents, and live wires. Cut the power and move up or evacuate early."),
                quiz(6, "What is the national emergency hotline in the Philippines?",
                        "117",
                        "143",
                        "911",
                        "8888",
                        "C", "911 is the national emergency number. 143 is the Philippine Red Cross, and 8888 is the citizens' complaint hotline."),
                quiz(7, "A fire starts in your house. Which statement is correct?",
                        "Use water on a burning cooking-oil pan",
                        "Stay low under the smoke and get out, then call for help",
                        "Go back inside to save your gadgets",
                        "Open all windows and doors to let the smoke out",
                        "B", "Smoke rises, so the cleaner air is near the floor. Never pour water on an oil fire; cover it with a lid instead."),
                quiz(8, "After a strong earthquake, you live near the coast and the sea suddenly pulls back. What should you do?",
                        "Go to the shore to look at the exposed seabed",
                        "Wait for an official announcement before moving",
                        "Move to high ground or inland immediately",
                        "Take photos for social media",
                        "C", "A sudden drop in sea level is a natural tsunami warning. Do not wait for sirens; go to high ground right away."),
                quiz(9, "Which of these is a warning sign of a possible landslide?",
                        "New cracks on the ground or slope and tilting trees or posts",
                        "Clear skies for several days",
                        "Birds flying in the morning",
                        "A dry riverbed during summer",
                        "A", "Cracks, bulging ground, tilted trees or posts, and muddy spring water on slopes mean the soil is moving. Leave the area and report it."),
                quiz(10, "When should a household agree on its family emergency plan?",
                        "Only after the barangay announces an evacuation",
                        "During the disaster itself",
                        "Before a disaster, and practise it regularly",
                        "There is no need for a plan",
                        "C", "Agree early on a meeting place, contact person, and evacuation route so everyone knows what to do even when phone lines are down."));
        quizQuestionRepository.saveAll(items);
        log.info("Loaded {} knowledge test questions.", items.size());
    }

    private static QuizQuestion quiz(int order, String question, String a, String b, String c, String d,
                                     String correct, String explanation) {
        return QuizQuestion.builder()
                .sortOrder(order)
                .question(question)
                .optionA(a)
                .optionB(b)
                .optionC(c)
                .optionD(d)
                .correctOption(correct)
                .explanation(explanation)
                .build();
    }

    private void seedGuides() {
        if (guideRepository.count() > 0) return;
        List<Guide> guides = List.of(
                guide(1, IncidentType.TYPHOON, "Typhoon",
                        "Tabunok is regularly hit by tropical cyclones from June to December. Watch the signal level raised by PAGASA for Cebu.",
                        """
                        Monitor PAGASA bulletins on radio, TV or the barangay page.
                        Trim tree branches near the house and secure the roof with ropes or sandbags.
                        Prepare a go-bag good for three days and charge phones and power banks.
                        Store drinking water and keep important documents in a sealed plastic bag.
                        Know the nearest evacuation center and the safest route going there.""",
                        """
                        Stay indoors and away from glass windows.
                        Unplug appliances and turn off the main switch if water starts coming in.
                        Follow the barangay's call for pre-emptive or forced evacuation.
                        Keep your radio on for updates; do not rely on social media rumours.""",
                        """
                        Wait for the all-clear before going home.
                        Watch out for fallen power lines, loose roofing, and weakened trees.
                        Report damage and injured persons to the barangay through this system.
                        Boil drinking water if the supply may be contaminated."""),
                guide(2, IncidentType.FLOOD, "Flooding (coastal and riverine)",
                        "Low-lying puroks near the coast and along the river can flood quickly during heavy rain or high tide.",
                        """
                        Know if your house is in a flood-prone area and where higher ground is.
                        Clean canals and drainage near your home before the rainy season.
                        Raise appliances, documents, and stored food above the expected water level.
                        Agree on a family meeting place in case members get separated.""",
                        """
                        Evacuate early when the barangay tells you to; do not wait for the water to rise.
                        Turn off electricity and LPG before leaving the house.
                        Never walk, swim, or drive through moving floodwater.
                        Keep away from rivers, canals, and the shoreline.""",
                        """
                        Return home only when officials say it is safe.
                        Have an electrician check wiring before switching the power back on.
                        Throw away food that touched floodwater and clean the house with disinfectant.
                        Watch for signs of leptospirosis such as fever and muscle pain, and see a doctor."""),
                guide(3, IncidentType.EARTHQUAKE, "Earthquake",
                        "Cebu sits near active faults. Earthquakes give no warning, so preparation happens before the shaking.",
                        """
                        Bolt tall cabinets and shelves to the wall; keep heavy items on low shelves.
                        Identify safe spots in each room, such as under a sturdy table.
                        Join the barangay and school earthquake drills.
                        Keep a flashlight and shoes beside your bed.""",
                        """
                        Duck, cover, and hold until the shaking stops.
                        Stay away from windows, hanging objects, and outside walls.
                        If outdoors, move to an open area away from buildings, posts, and wires.
                        If near the coast and the shaking is strong or long, move to high ground right away.""",
                        """
                        Expect aftershocks; leave the building calmly using the stairs.
                        Check yourself and others for injuries and give first aid.
                        Check for gas leaks and do not light matches.
                        Report damaged houses and roads to the barangay."""),
                guide(4, IncidentType.FIRE, "Fire",
                        "Houses in Tabunok are closely built, so a small fire can spread to neighbours within minutes.",
                        """
                        Do not overload extension cords and have old wiring checked.
                        Keep matches, lighters, and candles away from children.
                        Keep a fire extinguisher or a pail of sand near the kitchen.
                        Plan two ways out of every room.""",
                        """
                        Shout to warn others and call 911 or the fire station.
                        Stay low under the smoke and cover your nose with a damp cloth.
                        Feel doors with the back of your hand before opening them.
                        If your clothes catch fire: stop, drop, and roll.""",
                        """
                        Do not go back inside until firefighters say it is safe.
                        Seek medical help for burns and smoke inhalation.
                        Coordinate with the barangay for temporary shelter and relief.
                        Document losses for assistance and insurance claims."""),
                guide(5, IncidentType.LANDSLIDE, "Landslide (highway slopes)",
                        "Cut slopes along the highway and elevated areas can give way after days of continuous rain.",
                        """
                        Know if your area is marked as landslide-prone in the barangay hazard map.
                        Plant trees and grass on slopes and keep drainage clear.
                        Watch for cracks, bulging ground, and tilting trees or posts.""",
                        """
                        Leave immediately if you hear rumbling or see mud and rocks moving.
                        Move away from the path of the slide, not downhill along it.
                        Stay away from river channels where debris may flow.""",
                        """
                        Keep off the slide area; more slides may follow.
                        Check for injured or trapped persons and report them; do not dig alone.
                        Report broken roads, water lines, and power lines to the barangay."""));
        guideRepository.saveAll(guides);
        log.info("Loaded {} preparedness guides.", guides.size());
    }

    private static Guide guide(int order, IncidentType hazard, String title, String summary,
                               String before, String during, String after) {
        return Guide.builder()
                .sortOrder(order)
                .hazard(hazard)
                .title(title)
                .summary(summary)
                .beforeSteps(before.strip())
                .duringSteps(during.strip())
                .afterSteps(after.strip())
                .build();
    }

    private void seedHotlines() {
        if (hotlineRepository.count() > 0) return;
        hotlineRepository.saveAll(List.of(
                Hotline.builder().sortOrder(1).agency("National Emergency Hotline").number("911")
                        .description("Police, fire, and medical emergencies").build(),
                Hotline.builder().sortOrder(2).agency("Philippine Red Cross").number("143")
                        .description("Rescue, ambulance, and blood services").build()));
    }
}
